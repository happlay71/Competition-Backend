package online.happlay.jingsai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.StudentAwardDTO;
import online.happlay.jingsai.model.entity.Award;
import online.happlay.jingsai.model.entity.Student;
import online.happlay.jingsai.model.entity.StudentAward;
import online.happlay.jingsai.mapper.StudentAwardMapper;
import online.happlay.jingsai.model.query.StudentTeamQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentAwardVO;
import online.happlay.jingsai.service.IAwardService;
import online.happlay.jingsai.service.IStudentAwardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.happlay.jingsai.service.IStudentService;
import online.happlay.jingsai.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生获奖团队表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
@RequiredArgsConstructor
public class StudentAwardServiceImpl extends ServiceImpl<StudentAwardMapper, StudentAward> implements IStudentAwardService {

    private final IStudentService studentService;

    private final IAwardService awardService;

    private final JwtUtils jwtUtils;

    @Override
    public PaginationResultVO<StudentAwardVO> selectTeam(StudentTeamQuery studentTeamQuery, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);


        // 构建查询条件
        LambdaQueryWrapper<StudentAward> queryWrapper = new LambdaQueryWrapper<>();

        // 如果不是管理员，只能查询自己的获奖信息
        if (!isAdmin) {
            // 获取当前学生ID
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.eq(Student::getUserId, currentUserId);
            Student student = studentService.getOne(studentQueryWrapper);

            if (student != null) {
                // 获取当前学生的获奖ID（假设学生只能有一个奖项）
                LambdaQueryWrapper<StudentAward> studentAwardQueryWrapper = new LambdaQueryWrapper<>();
                studentAwardQueryWrapper.eq(StudentAward::getStudentId, student.getId());
                List<StudentAward> studentAwards = this.list(studentAwardQueryWrapper);

                // 如果没有找到对应的获奖记录，直接返回空结果
                if (studentAwards.isEmpty()) {
                    return new PaginationResultVO<>(0, studentTeamQuery.getPageSize(), studentTeamQuery.getPageNo(), 0, new ArrayList<>());
                }

                // 获取获奖ID列表
                List<Integer> awardIds = studentAwards.stream()
                        .map(StudentAward::getAwardId)
                        .collect(Collectors.toList());

                // 查询与当前学生相关的第一名学生的获奖ID
                LambdaQueryWrapper<Award> firstPlaceQueryWrapper = new LambdaQueryWrapper<>();
                firstPlaceQueryWrapper.eq(Award::getFirstPlaceStudentId, student.getId());
                List<Award> firstPlaceAwards = awardService.list(firstPlaceQueryWrapper);

                // 将第一名学生的获奖ID添加到列表中
                if (!firstPlaceAwards.isEmpty()) {
                    List<Integer> firstPlaceAwardIds = firstPlaceAwards.stream()
                            .map(Award::getId)
                            .collect(Collectors.toList());
                    awardIds.addAll(firstPlaceAwardIds);
                }

                // 去重
                awardIds = awardIds.stream().distinct().collect(Collectors.toList());

                // 根据获奖ID查找所有属于同一团队的学生获奖信息
                queryWrapper.in(StudentAward::getAwardId, awardIds);
            } else {
                return new PaginationResultVO<>(0, studentTeamQuery.getPageSize(), studentTeamQuery.getPageNo(), 0, new ArrayList<>());
            }
        }

        // 如果学生姓名存在，则根据学生姓名查询所有符合姓名的学生id集合
        if (StrUtil.isNotBlank(studentTeamQuery.getStudentName())) {
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.like(Student::getName, studentTeamQuery.getStudentName());

            // 获取符合条件的学生ID集合
            List<Long> studentIds = studentService.list(studentQueryWrapper)
                    .stream()
                    .map(Student::getId)
                    .collect(Collectors.toList());

            if (!studentIds.isEmpty()) {
                queryWrapper.in(StudentAward::getStudentId, studentIds);  // 将学生ID集合作为查询条件
            } else {
                return new PaginationResultVO<>(0, studentTeamQuery.getPageSize(), studentTeamQuery.getPageNo(), 0, new ArrayList<>()); // 没有符合的学生，返回空列表
            }
        }

        // 如果获奖id存在，则加入查询条件
        if (studentTeamQuery.getAwardId() != null) {
            queryWrapper.eq(StudentAward::getAwardId, studentTeamQuery.getAwardId());
        }





        // 分页查询
        Page<StudentAward> page = new Page<>(studentTeamQuery.getPageNo(), studentTeamQuery.getPageSize());
        Page<StudentAward> studentAwardPage = this.page(page, queryWrapper);

        // 获取总记录数
        long totalCount = studentAwardPage.getTotal();

        // 转换查询结果为VO对象
        List<StudentAwardVO> studentAwardVOList = studentAwardPage.getRecords()
                .stream()
                .map(this::convertToStudentAwardVO) // 假设你有一个方法转换为VO对象
                .collect(Collectors.toList());

        // 返回分页结果
        return new PaginationResultVO<>(
                (int) totalCount, // 总记录数
                studentTeamQuery.getPageSize(), // 每页大小
                studentTeamQuery.getPageNo(), // 当前页码
                (int) studentAwardPage.getPages(), // 总页数
                studentAwardVOList // 学生团队奖项信息列表
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeam(Integer id, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 获取当前学生ID
        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.eq(Student::getUserId, currentUserId);
        Student student = studentService.getOne(studentQueryWrapper);

        // 如果没有找到当前学生信息，抛出异常或返回空结果
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "未找到学生信息");


        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);


        // 如果不是管理员，校验团队ID是否与当前学生的ID匹配
        if (!isAdmin) {
            // 校验申请ID是否是当前学生的ID（假设 award 表中的 studentId 是团队ID）
            Long studentIdFromAward = this.getById(id).getStudentId(); // 获取团队ID对应的学生ID
            if (!Objects.equals(studentIdFromAward, student.getId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
            }
        }

        // 根据团队ID查找相关奖项记录
        LambdaQueryWrapper<StudentAward> awardQueryWrapper = new LambdaQueryWrapper<>();
        awardQueryWrapper.eq(StudentAward::getId, id);  // 通过awardId查找相关的所有学生获奖记录

        List<StudentAward> studentAwards = this.list(awardQueryWrapper);

        // 如果没有找到相关的奖项记录，抛出异常
        ThrowUtils.throwIf(studentAwards.isEmpty(), ErrorCode.NOT_FOUND_ERROR,"未找到与该奖项记录");

        // 删除相关奖项记录
        this.remove(awardQueryWrapper);
    }

    @Override
    public void saveOrUpdateTeam(StudentAwardDTO studentAwardDTO, HttpServletRequest request) {
        // 根据获奖id查询是否存在该获奖信息
        Integer awardId = studentAwardDTO.getAwardId();
        Award award = awardService.getById(awardId);

        // 如果获奖信息不存在，抛出异常
        ThrowUtils.throwIf(award == null, ErrorCode.PARAMS_ERROR, "获奖信息不存在");

        // 判断该获奖状态status是否为1，只有通过审核的才能新增团队成员
        ThrowUtils.throwIf(award.getStatus() != 1, ErrorCode.FORBIDDEN_ERROR, "该获奖信息尚未通过审核，无法新增或修改团队成员");

//        // 获取获奖信息中的学生id
        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.eq(Student::getStudentId, studentAwardDTO.getStudentId());
        Student student = studentService.getOne(studentQueryWrapper);
        ThrowUtils.throwIf(student == null, ErrorCode.PARAMS_ERROR, "该学生信息不存在");

        // 根据id判断是更新还是新增
        if (studentAwardDTO.getId() != null) {
            // 更新操作
            // 更新之前可以进行一些额外的验证，比如该学生是否已经是该团队成员等
            StudentAward existingAward = this.getById(studentAwardDTO.getId());
            ThrowUtils.throwIf(existingAward == null, ErrorCode.PARAMS_ERROR, "该团队成员信息不存在，无法更新");

            // 进行更新
            existingAward.setStudentId(student.getId());
            existingAward.setAwardId(awardId);
            existingAward.setRankingInTeam(studentAwardDTO.getRankingInTeam());

            this.updateById(existingAward);
        } else {

            StudentAward newStudentAward = new StudentAward();
            newStudentAward.setStudentId(student.getId());
            newStudentAward.setAwardId(awardId);
            newStudentAward.setRankingInTeam(studentAwardDTO.getRankingInTeam());
            this.save(newStudentAward);
        }
    }


    private StudentAwardVO convertToStudentAwardVO(StudentAward studentAward) {
        StudentAwardVO studentAwardVO = BeanUtil.copyProperties(studentAward, StudentAwardVO.class);
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getId, studentAward.getStudentId());
        Student student = studentService.getOne(queryWrapper);
        studentAwardVO.setStudentName(student.getName());
        return studentAwardVO;
    }

}
