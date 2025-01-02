package online.happlay.jingsai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.entity.Major;
import online.happlay.jingsai.mapper.MajorMapper;
import online.happlay.jingsai.model.entity.Student;
import online.happlay.jingsai.model.query.MajorQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.IMajorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.happlay.jingsai.service.IStudentService;
import online.happlay.jingsai.utils.JwtUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 专业表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
@RequiredArgsConstructor
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements IMajorService {

    @Lazy
    @Resource
    private IStudentService studentService;

    private final JwtUtils jwtUtils;

    @Override
    public PaginationResultVO<Major> selectMajor(MajorQuery majorQuery, HttpServletRequest request) {

        // 1. 构建查询条件
        LambdaQueryWrapper<Major> queryWrapper = new LambdaQueryWrapper<>();

        // 如果有专业名称，加入模糊查询条件
        if (majorQuery.getName() != null && !majorQuery.getName().isEmpty()) {
            queryWrapper.like(Major::getName, majorQuery.getName());
        }

        // 2. 分页查询
        Page<Major> page = new Page<>(majorQuery.getPageNo(), majorQuery.getPageSize());
        Page<Major> majorPage = this.page(page, queryWrapper);

        // 获取总记录数
        long totalCount = majorPage.getTotal();

        // 3. 转换查询结果为 VO 对象（假设有转换方法）
        List<Major> majorList = majorPage.getRecords();

        // 4. 返回分页结果
        return new PaginationResultVO<>(
                (int) totalCount, // 总记录数
                majorQuery.getPageSize(), // 每页大小
                majorQuery.getPageNo(), // 当前页码
                (int) Math.ceil((double) totalCount / majorQuery.getPageSize()), // 总页数
                majorList // 专业列表
        );
    }

    @Override
    public void saveOrUpdateMajor(Major major, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员，校验申请ID是否是当前用户
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        // 1. 判断是否传入了 id
        if (major.getId() != null) {
            // 2. 如果 id 存在，则执行更新操作
            // 这里假设更新的是 name、description、status 等字段
            Major existingMajor = this.getById(major.getId());

            ThrowUtils.throwIf(existingMajor == null , ErrorCode.NOT_FOUND_ERROR, "未找到指定的专业");

            // 执行更新操作
            existingMajor.setName(major.getName());
            this.updateById(existingMajor);

        } else {
            // 3. 如果没有 id，则执行新增操作
            // 判断专业名称是否已存在，避免重复插入
            LambdaQueryWrapper<Major> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Major::getName, major.getName());

            Major existingMajor = this.getOne(queryWrapper);
            ThrowUtils.throwIf(existingMajor != null, ErrorCode.PARAMS_ERROR, "该专业名称已存在");

            // 设置创建时间
            major.setName(major.getName());
            major.setAwardCount(0);

            // 执行新增操作
            this.save(major);
        }
    }

    @Override
    public void deleteMajor(Integer id, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员，校验申请ID是否是当前用户
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getProfession, id);
        Student student = studentService.getOne(queryWrapper);
        ThrowUtils.throwIf(student != null, ErrorCode.PARAMS_ERROR, "该专业下有学生，无法删除");

        this.removeById(id);
    }

    @Override
    public List<String> selectMajorName(HttpServletRequest request) {
        // 查询所有的专业名称
        LambdaQueryWrapper<Major> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Major::getName);  // 假设Major类中有一个`majorName`字段，查询专业名称

        // 执行查询，获取所有专业信息
        List<Major> majorList = this.list(queryWrapper);

        // 提取专业名称列表并去重
        return majorList.stream()
                .map(Major::getName)   // 获取每个专业名称
                .distinct()                 // 去重
                .collect(Collectors.toList()); // 转换为List并返回
    }



}
