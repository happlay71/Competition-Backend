package online.happlay.jingsai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.enums.UserStatusEnum;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.*;
import online.happlay.jingsai.model.entity.Student;
import online.happlay.jingsai.model.entity.User;
import online.happlay.jingsai.mapper.UserMapper;
import online.happlay.jingsai.model.query.UserQuery;
import online.happlay.jingsai.model.vo.LoginUserVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.UserVO;
import online.happlay.jingsai.service.IStudentService;
import online.happlay.jingsai.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.happlay.jingsai.utils.JwtUtils;
import online.happlay.jingsai.utils.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final JwtUtils jwtUtils;

    private final StringUtils stringUtils;

    private final IStudentService studentService;

    @Override
    public void register(RegisterDTO registerDto) {
        ThrowUtils.throwIf(registerDto.getId() != null, ErrorCode.PARAMS_ERROR, "无效ID");
        ThrowUtils.throwIf(this.getById(registerDto.getId()) != null, ErrorCode.USER_ALREADY_EXISTS);

        // 判断账户和密码都不为空
        ThrowUtils.throwIf(registerDto.getUserAccount() == null, ErrorCode.PARAMS_ERROR, "账户不能为空");
        User user = new User();
        user.setUserAccount(registerDto.getUserAccount());
        user.setUsername(registerDto.getUsername());
        String newPassword = stringUtils.encryptPassword(registerDto.getPassword());
        user.setPassword(newPassword);
        this.save(user);
    }

    @Override
    public LoginUserVO login(LoginUserDTO loginUserDTO) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, loginUserDTO.getUserAccount());
        User user = this.getOne(queryWrapper);

        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!stringUtils.matches(loginUserDTO.getPassword(), user.getPassword()), ErrorCode.PARAMS_ERROR, "密码错误");

        boolean statusById = checkUserStatusById(String.valueOf(user.getId()));
        ThrowUtils.throwIf(statusById, ErrorCode.USER_DISABLE_DELETE);

        LoginUserVO userVO = BeanUtil.copyProperties(user, LoginUserVO.class);
        String token = jwtUtils.createToken(String.valueOf(user.getId()), user.getRole());
        userVO.setToken(token);
        return userVO;
    }

    @Override
    public boolean checkLoginUserById(String userId) {
        Long id = Long.valueOf(userId);
        User user = this.getById(id);
        return user == null;
    }

    @Override
    public boolean checkUserStatusById(String userId) {
        Long id = Long.valueOf(userId);
        User user = this.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户信息不存在");

        return Objects.equals(user.getStatus(), UserStatusEnum.DISABLE.getStatus()) || user.getIsDelete().equals(1);
    }

    @Override
    public PaginationResultVO<UserVO> selectUser(UserQuery userQuery, HttpServletRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 如果用户名称不为空，则加入用户名称查询条件
        if (StrUtil.isNotBlank(userQuery.getUsername())) {
            queryWrapper.like(User::getUsername, userQuery.getUsername());
        }

        // 如果账号名不为空，则加入账号名查询条件
        if (StrUtil.isNotBlank(userQuery.getUserAccount())) {
            queryWrapper.like(User::getUserAccount, userQuery.getUserAccount());
        }

        // 如果用户状态不为空，则加入用户状态查询条件
        if (userQuery.getStatus() != null) {
            queryWrapper.eq(User::getStatus, userQuery.getStatus());
        }

        // 分页查询
        Page<User> page = new Page<>(userQuery.getPageNo(), userQuery.getPageSize());
        Page<User> userPage = this.page(page, queryWrapper);

        // 获取总记录数
        long totalCount = userPage.getTotal();

        // 将查询结果转换为 VO 对象
        List<UserVO> userVOList = userPage.getRecords()
                .stream()
                .map(this::convertToUserVO)  // 假设有一个方法 convertToUserVO 用于转换实体到 VO
                .collect(Collectors.toList());

        // 返回分页结果
        return new PaginationResultVO<>(
                (int) totalCount,  // 总记录数
                userQuery.getPageSize(),  // 每页大小
                userQuery.getPageNo(),    // 当前页码
                (int) userPage.getPages(),  // 总页数
                userVOList  // 用户列表
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePassword(PasswordDTO passwordDTO, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request); // 获取当前用户ID
        String currentUserRole = jwtUtils.getUserRole(request); // 获取当前用户角色

        // 判断当前用户是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员，且目标ID与当前用户ID不一致，则无权限修改
        if (!isAdmin && !currentUserId.equals(String.valueOf(passwordDTO.getId()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限修改该用户密码");
        }

        // 获取目标用户的信息
        User user = this.getById(passwordDTO.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 获取请求中传递的新密码（假设是通过请求的参数传递）
        String password = passwordDTO.getPassword();
        if (StrUtil.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }

        String newPassword = stringUtils.encryptPassword(password);
        user.setPassword(newPassword);

        // 保存更新
        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUserVO saveUser(UserDTO userDTO, HttpServletRequest request) {
        // 获取当前用户的ID和角色（可以用来判断当前用户是否有权限执行此操作）
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 修改用户操作，首先检查用户是否存在
        User existingUser = this.getById(userDTO.getId());
        ThrowUtils.throwIf(existingUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 根据用户ID判断是新增还是修改用户
        if (userDTO.getId() != null) {

            // 权限校验：普通用户只能修改自己的信息，管理员可以修改其他人的信息
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                if (!currentUserId.equals(String.valueOf(userDTO.getId()))) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限修改用户信息");
                }
                // 普通用户不能将自己设置为管理员
                if ("admin".equalsIgnoreCase(userDTO.getRole())) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限修改角色信息");
                }
            }

            // 管理员不能修改自己的角色
            if ("ADMIN".equalsIgnoreCase(currentUserRole) && currentUserId.equals(String.valueOf(userDTO.getId())) && !existingUser.getRole().equalsIgnoreCase(userDTO.getRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "管理员不能修改自己的角色");
            }
            // 更新操作，检查是否更改了账号（避免账号重复）
            if (!existingUser.getUserAccount().equals(userDTO.getUserAccount())) {
                checkAccountExist(userDTO.getUserAccount());
            }

            // 更新用户信息
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setUserAccount(userDTO.getUserAccount());
            existingUser.setRole(userDTO.getRole()); // 根据角色进行更新
            this.updateById(existingUser);

        }

        return BeanUtil.copyProperties(existingUser, LoginUserVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(UserStatusDTO userStatusDTO, HttpServletRequest request) {
        // 从请求中获取当前用户的角色
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员
        if (!isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        User user = this.getById(userStatusDTO.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        user.setStatus(userStatusDTO.getStatus());
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Integer id, HttpServletRequest request) {
        // 获取当前用户ID和角色（用于权限判断）
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 检查用户是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 获取用户信息，检查是否存在
        User user = this.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");

        // 如果是管理员，不能删除自己
        if (isAdmin && currentUserId.equals(String.valueOf(id))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "管理员不能删除自己");
        }

        // 删除前先检查该用户是否认证了学生信息
        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.eq(Student::getUserId, Long.valueOf(id));
        Student student = studentService.getOne(studentQueryWrapper);

        // 如果该用户有认证的学生信息，执行取消认证操作
        if (student != null) {
            // 使用 UpdateWrapper 来更新 userId 为 null
            UpdateWrapper<Student> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", Long.valueOf(id)); // 使用条件来定位要更新的记录
            updateWrapper.set("user_id", null); // 设置 userId 为 null

            // 执行更新操作
            boolean updateResult = studentService.update(updateWrapper);
            if (!updateResult) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "取消学生认证失败");
            }

        }

        // 删除用户信息（软删除）
        UpdateWrapper<User> updateUserWrapper = new UpdateWrapper<>();
        updateUserWrapper.eq("id", id); // 根据用户ID定位
        updateUserWrapper.set("isDelete", 1); // 设置 is_delete 字段为 1

        this.update(updateUserWrapper); // 执行更新操作
//        this.removeById(id);
    }

    @Override
    public void updatePassword(UpPWDTO upPWDTO, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request); // 获取当前用户ID

        // 如果不是管理员，且目标ID与当前用户ID不一致，则无权限修改
        if (!currentUserId.equals(String.valueOf(upPWDTO.getId()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限修改该用户密码");
        }

        // 获取目标用户的信息
        User user = this.getById(upPWDTO.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        if (!stringUtils.matches(upPWDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
        }

        // 获取请求中传递的新密码（假设是通过请求的参数传递）
        String password = upPWDTO.getNewPassword();
        if (StrUtil.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }

        if (upPWDTO.getOldPassword().equals(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能与旧密码相同");
        }


        String newPassword = stringUtils.encryptPassword(password);
        user.setPassword(newPassword);

        // 保存更新
        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码更新失败");
        }
    }




    /**
     * 检查账户是否已经存在
     */
    private void checkAccountExist(String userAccount) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User existingUser = this.getOne(queryWrapper);
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "该账号已存在");
        }
    }



    private UserVO convertToUserVO(User user) {
        // 其他字段的转换
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setPassword(null);
        return userVO;
    }


}
