package online.happlay.jingsai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.StudentDTO;
import online.happlay.jingsai.model.dto.StudentSaveDTO;
import online.happlay.jingsai.model.entity.*;
import online.happlay.jingsai.mapper.StudentMapper;
import online.happlay.jingsai.model.excel.StudentExcel;
import online.happlay.jingsai.model.query.StudentQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentVO;
import online.happlay.jingsai.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.happlay.jingsai.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    private final JwtUtils jwtUtils;

    private final IMajorService majorService;

    @Lazy
    @Resource
    private IUserService userService;

    @Lazy
    @Resource
    private IAwardService awardService;

    @Lazy
    @Resource
    private IStudentAwardService studentAwardService;

    private final StudentMapper baseMapper;

    @Override
    public void verify(StudentDTO studentDTO, HttpServletRequest request) {
        // 查询该用户是否验证过
        String userId = jwtUtils.getUserId(request);
        Student isStudent = this.getOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getUserId, Long.valueOf(userId)));
        ThrowUtils.throwIf(isStudent != null, ErrorCode.PARAMS_ERROR, "用户已认证");

        // 1. 校验学生信息是否合法（例如：学号是否存在，姓名和邮箱是否匹配等）
        ThrowUtils.throwIf(studentDTO.getStudentId() == null || studentDTO.getStudentId().isEmpty(),
                ErrorCode.PARAMS_ERROR, "学生学号不能为空");

        // 2. 从数据库查询学生信息
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStudentId, studentDTO.getStudentId());
        Student student = this.getOne(queryWrapper);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "未找到该学生");
        ThrowUtils.throwIf(student.getUserId() != null, ErrorCode.PARAMS_ERROR, "该学生已被认证");

        // 3. 验证学生提供的信息与数据库中存储的信息是否一致
        ThrowUtils.throwIf(!student.getName().equals(studentDTO.getName()), ErrorCode.PARAMS_ERROR, "学生姓名不匹配");
        ThrowUtils.throwIf(!student.getEmail().equals(studentDTO.getEmail()), ErrorCode.PARAMS_ERROR, "学生邮箱不匹配");

        // 4. 如果学生信息验证通过，进行用户关联
        student.setUserId(studentDTO.getId());
        this.updateById(student);
    }

    @Override
    public StudentVO info(HttpServletRequest request) {
        // 1. 获取当前登录用户的 ID，假设通过请求的 Header 或者 Token 获取
        Long userId = Long.valueOf(jwtUtils.getUserId(request));

        // 2. 根据用户 ID 查询学生信息
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getUserId, userId);
        Student student = this.getOne(queryWrapper);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "未找到学生信息");

        // 3. 将 Student 对象转换为 StudentVO 对象
        StudentVO studentVO = BeanUtil.copyProperties(student, StudentVO.class);
        Major major = majorService.getById(student.getProfession());
        studentVO.setProfession(major.getName());
        return studentVO;
    }

    @Override
    public PaginationResultVO<StudentVO> selectStudent(StudentQuery studentQuery, HttpServletRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();

        // 如果有学生学号，则加入查询条件
        if (studentQuery.getStudentId() != null && !studentQuery.getStudentId().isEmpty()) {
            queryWrapper.like(Student::getStudentId, studentQuery.getStudentId());
        }

        // 如果有学生姓名，则加入查询条件
        if (studentQuery.getStudentName() != null && !studentQuery.getStudentName().isEmpty()) {
            queryWrapper.like(Student::getName, studentQuery.getStudentName());
        }

        // 如果有专业名称，则加入条件查询（假设需要通过专业名称查询）
        if (studentQuery.getProfession() != null && !studentQuery.getProfession().isEmpty()) {
            LambdaQueryWrapper<Major> majorQueryWrapper = new LambdaQueryWrapper<>();
            majorQueryWrapper.like(Major::getName, studentQuery.getProfession());
            Major major = majorService.getOne(majorQueryWrapper);
            if (major != null) {
                queryWrapper.eq(Student::getProfession, major.getId());
            }
        }

        // 如果有认证状态，则加入条件查询
        if (studentQuery.getCertification() != null) {
            switch (studentQuery.getCertification()) {
                case "pass":
                    queryWrapper.isNotNull(Student::getUserId); // 已认证，查询 userId 不为 null 的学生
                    break;
                case "pending":
                    queryWrapper.isNull(Student::getUserId); // 未认证，查询 userId 为 null 的学生
                    break;
                default:
                    break;
            }
        }

        // 2. 分页查询
        Page<Student> page = new Page<>(studentQuery.getPageNo(), studentQuery.getPageSize());
        Page<Student> studentPage = this.page(page, queryWrapper);

        // 获取总记录数
        long totalCount = studentPage.getTotal();

        // 3. 转换查询结果为 VO 对象
        List<StudentVO> studentVOList = studentPage.getRecords().stream()
                .map(this::convertToStudentVO)
                .collect(Collectors.toList());


        // 4. 返回分页结果
        return new PaginationResultVO<>(
                (int) totalCount, // 总记录数
                studentQuery.getPageSize(), // 每页大小
                studentQuery.getPageNo(), // 当前页码
                (int) Math.ceil((double) totalCount / studentQuery.getPageSize()), // 总页数
                studentVOList // 学生信息列表
        );
    }

    @Override
    public void saveOrUpdateStudent(StudentSaveDTO studentSaveDTO, HttpServletRequest request) {
        // 从请求中获取当前用户的角色
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        // 根据是否传入id来判断是新增还是修改
        if (studentSaveDTO.getId() == null) {
            // 新增操作
            this.addStudent(studentSaveDTO);
        } else {
            // 修改操作
            this.updateStudent(studentSaveDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void status(Long id, Integer status, HttpServletRequest request) {
        // 从请求中获取当前用户的角色
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        if (currentUserId.equals(String.valueOf(id))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "管理员不能修改自己状态");
        }

        Student student = this.getById(id);
        User user = userService.getById(student.getUserId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 改变用户状态
        user.setStatus(status);
        userService.updateById(user);
    }

    private void addStudent(StudentSaveDTO studentSaveDTO) {
        // 1. 根据专业名称找到对应的专业 ID
        String professionName = studentSaveDTO.getProfession();
        Major major = majorService.getOne(new LambdaQueryWrapper<Major>().eq(Major::getName, professionName));
        if (major == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "专业不存在");
        }

        // 2. 创建学生对象并填充信息
        Student student = new Student();
        student.setStudentId(studentSaveDTO.getStudentId());
        student.setName(studentSaveDTO.getName());
        student.setEmail(studentSaveDTO.getEmail());
        student.setPhone(studentSaveDTO.getPhone());
        student.setProfession(major.getId()); // 设置专业 ID
        student.setGrade(studentSaveDTO.getGrade()); // 设置年级
        student.setGender(studentSaveDTO.getGender());

        // 3. 保存学生信息
        this.save(student);
    }

    private void updateStudent(StudentSaveDTO studentSaveDTO) {
        // 1. 根据学生 ID 查询学生信息
        Student student = this.getById(studentSaveDTO.getId());
        if (student == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生信息不存在");
        }

        // 2. 根据专业名称找到对应的专业 ID
        String professionName = studentSaveDTO.getProfession();
        Major major = majorService.getOne(new LambdaQueryWrapper<Major>().eq(Major::getName, professionName));
        if (major == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "专业不存在");
        }

        // 3. 判断学生的专业是否有变化
        if (!major.getId().equals(student.getProfession())) {
            // 旧的专业 ID
            Integer oldMajorId = student.getProfession();

            // 4. 查找获奖表中第一学生id是该学生id，统计个数
            LambdaQueryWrapper<Award> awardQueryWrapper = new LambdaQueryWrapper<>();
            awardQueryWrapper.eq(Award::getFirstPlaceStudentId, student.getId());

            // 获奖数量统计
            Integer awardCount = Math.toIntExact(awardService.count(awardQueryWrapper));

            // 5. 把获奖数在旧的专业里减去
            if (awardCount > 0) {
                Major oldMajor = majorService.getById(oldMajorId);
                if (oldMajor != null) {
                    oldMajor.setAwardCount(oldMajor.getAwardCount() - awardCount);
                    majorService.updateById(oldMajor); // 更新旧专业的获奖数
                }
            }

            // 6. 把获奖数在新的专业里加上
            if (awardCount > 0) {
                Major newMajor = majorService.getById(major.getId());
                if (newMajor != null) {
                    newMajor.setAwardCount(newMajor.getAwardCount() + awardCount);
                    majorService.updateById(newMajor); // 更新新专业的获奖数
                }
            }
        }

        // 3. 更新学生信息
        student.setStudentId(studentSaveDTO.getStudentId());
        student.setName(studentSaveDTO.getName());
        student.setEmail(studentSaveDTO.getEmail());
        student.setPhone(studentSaveDTO.getPhone());
        student.setProfession(major.getId()); // 设置专业 ID
        student.setGrade(studentSaveDTO.getGrade()); // 设置年级
        student.setGender(studentSaveDTO.getGender());

        // 4. 更新学生信息
        this.updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Integer id, HttpServletRequest request) {
        // 1. 校验学生ID是否存在
        Student student = this.getById(id);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "学生信息不存在");

        // 2. 检查该学生是否存在相关的奖项记录
        LambdaQueryWrapper<StudentAward> sAwardQueryWrapper = new LambdaQueryWrapper<>();
        sAwardQueryWrapper.eq(StudentAward::getStudentId, id);
        List<StudentAward> studentAwards = studentAwardService.list(sAwardQueryWrapper);

        // 3. 如果该学生有奖项信息，不能删除（或者可以选择其他处理方式）
        if (!studentAwards.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该学生有团队获奖信息，无法删除");
        }

        LambdaQueryWrapper<Award> awardQueryWrapper = new LambdaQueryWrapper<>();
        awardQueryWrapper.eq(Award::getFirstPlaceStudentId, id);
        List<Award> awardList = awardService.list(awardQueryWrapper);

        if (!awardList.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该学生有获奖信息，无法删除");
        }

        // 4. 执行删除操作，删除学生信息
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importStudentByExcel(MultipartFile file, HttpServletRequest request) {
        try {
            // 读取 Excel 文件
            EasyExcel.read(file.getInputStream(), StudentExcel.class, new AnalysisEventListener<StudentExcel>() {
                private List<Student> studentList = new ArrayList<>();

                @Override
                public void invoke(StudentExcel studentExcel, AnalysisContext context) {
                    try {
                        // 数据校验和转换
                        validateStudent(studentExcel);

                        // 转换为 Student 实体
                        Student student = new Student();
                        BeanUtils.copyProperties(studentExcel, student);

                        // 根据专业名称查找专业ID
                        Major major = majorService.getOne(new LambdaQueryWrapper<Major>().eq(Major::getName, studentExcel.getProfession()));
                        if (major != null) {
                            student.setProfession(major.getId());
                        } else {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "专业不存在");
                        }

                        studentList.add(student);

                        // 达到一定数量批量插入
                        if (studentList.size() >= 100) {
                            saveStudentBatch(studentList);
                            studentList.clear();
                        }
                    } catch (Exception e) {
                        // 捕获异常并记录日志，便于排查问题
                        System.err.println("导入学生信息时发生错误: " + e.getMessage());
                        e.printStackTrace(); // 打印完整堆栈信息
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    try {
                        // 处理剩余数据
                        if (!studentList.isEmpty()) {
                            saveStudentBatch(studentList);
                        }
                    } catch (Exception e) {
                        System.err.println("批量保存剩余学生信息时发生错误: " + e.getMessage());
                        e.printStackTrace(); // 打印完整堆栈信息
                    }
                }
            }).sheet().doRead(); // 执行 Excel 解析

        } catch (IOException e) {
            // 读取文件异常
            System.err.println("读取文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Excel文件读取失败");
        } catch (Exception e) {
            // 捕获其他未知异常
            System.err.println("导入过程中发生未知错误: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "导入过程中发生未知错误");
        }
    }



    /**
     * 批量保存学生信息
     */
    private void saveStudentBatch(List<Student> studentList) {
        baseMapper.insertBatch(studentList);
    }

    /**
     * 验证学生信息
     */
    private void validateStudent(StudentExcel studentExcel) {
        // 1. 必填字段校验
        ThrowUtils.throwIf(StringUtils.isBlank(studentExcel.getStudentId()),
                ErrorCode.PARAMS_ERROR, "学号不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(studentExcel.getName()),
                ErrorCode.PARAMS_ERROR, "姓名不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(studentExcel.getProfession()),
                ErrorCode.PARAMS_ERROR, "专业不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(studentExcel.getEmail()),
                ErrorCode.PARAMS_ERROR, "邮箱不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(studentExcel.getPhone()),
                ErrorCode.PARAMS_ERROR, "电话不能为空");

        // 2. 格式校验
        String studentId = studentExcel.getStudentId();
        ThrowUtils.throwIf(!studentId.matches("^\\d{7,12}$"),
                ErrorCode.PARAMS_ERROR, "学号格式错误");

        String email = studentExcel.getEmail();
        ThrowUtils.throwIf(!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$"),
                ErrorCode.PARAMS_ERROR, "邮箱格式错误");

        String phone = studentExcel.getPhone();
        ThrowUtils.throwIf(!phone.matches("^1[3-9]\\d{9}$"),
                ErrorCode.PARAMS_ERROR, "手机号格式错误");

        // 3. 重复校验
        ThrowUtils.throwIf(baseMapper.existsByStudentId(studentId),
                ErrorCode.PARAMS_ERROR, "学号已存在");
    }


    private StudentVO convertToStudentVO(Student student) {
        // 创建一个 StudentVO 对象用于返回
        StudentVO studentVO = BeanUtil.copyProperties(student, StudentVO.class);

        // 2. 根据专业ID查找对应的专业名称
        Major major = majorService.getById(student.getProfession());
        if (major != null) {
            studentVO.setProfession(major.getName());  // 假设这里的 'grade' 对应的是专业名称
        }

        // 3. 判断是否有userId来判断学生认证状态
        if (student.getUserId() != null) {
            studentVO.setCertification(1);
            // 如果有userId，查询对应的用户信息中的状态信息
            User user = userService.getById(student.getUserId());
            if (user != null) {
                // 假设用户表中有一个状态字段status，表示用户的激活状态
                studentVO.setStatus(user.getStatus());
            }
        } else {
            studentVO.setCertification(0);
        }

        return studentVO;
    }



}
