package online.happlay.jingsai.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.annotation.AuthCheck;
import online.happlay.jingsai.common.BaseResponse;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.common.ResultUtils;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.*;
import online.happlay.jingsai.model.query.AwardQuery;
import online.happlay.jingsai.model.query.UserQuery;
import online.happlay.jingsai.model.vo.AwardVO;
import online.happlay.jingsai.model.vo.LoginUserVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.UserVO;
import online.happlay.jingsai.service.IUserService;
import online.happlay.jingsai.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.security.util.Password;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "用户")
public class UserController {

    private final IUserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public BaseResponse<String> register(@RequestBody RegisterDTO registerDto) {
        ThrowUtils.throwIf(registerDto == null, ErrorCode.NOT_FOUND_ERROR);
        userService.register(registerDto);
        return ResultUtils.success("注册成功");
    }

    @PostMapping("/login")
    @AuthCheck(checkLogin = false)
    @ApiOperation(value = "用户登录")
    public BaseResponse<LoginUserVO> login(@RequestBody LoginUserDTO loginUserDTO) {
        ThrowUtils.throwIf(loginUserDTO == null, ErrorCode.NOT_FOUND_ERROR);
        LoginUserVO userVO = userService.login(loginUserDTO);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/selectUser")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "查询用户信息")
    public BaseResponse<PaginationResultVO<UserVO>> selectUser(@RequestBody UserQuery userQuery,
                                                               HttpServletRequest request) {
        PaginationResultVO<UserVO> userVOList = userService.selectUser(userQuery, request);
        return ResultUtils.success(userVOList);
    }

    @PostMapping("/savePassword")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "修改用户密码")
    public BaseResponse<String> savePassword(@RequestBody PasswordDTO passwordDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(passwordDTO == null, ErrorCode.NOT_FOUND_ERROR);
        userService.savePassword(passwordDTO, request);
        return ResultUtils.success("新密码保存成功");
    }

    @PostMapping("/updatePassword")
    @AuthCheck()
    @ApiOperation(value = "修改用户密码")
    public BaseResponse<String> updatePassword(@RequestBody UpPWDTO upPWDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(upPWDTO == null, ErrorCode.NOT_FOUND_ERROR);
        userService.updatePassword(upPWDTO, request);
        return ResultUtils.success("新密码保存成功");
    }

    @PostMapping("/saveUser")
    @AuthCheck()
    @ApiOperation(value = "修改用户信息")
    public BaseResponse<LoginUserVO> saveUser(@RequestBody UserDTO userDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(userDTO == null, ErrorCode.NOT_FOUND_ERROR);
        LoginUserVO loginUserVO = userService.saveUser(userDTO, request);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/updateStatus")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "修改用户状态")
    public BaseResponse<String> updateStatus(@RequestBody UserStatusDTO userStatusDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(userStatusDTO == null, ErrorCode.NOT_FOUND_ERROR);
        userService.updateStatus(userStatusDTO, request);
        return ResultUtils.success("用户状态修改成功");
    }

    @GetMapping("/deleteUser")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "删除用户信息")
    public BaseResponse<String> deleteUser(@RequestParam("id") Integer id,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "用户ID不存在");
        userService.deleteUser(id, request);
        return ResultUtils.success("用户信息删除成功");
    }
}
