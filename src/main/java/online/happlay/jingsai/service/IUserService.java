package online.happlay.jingsai.service;

import online.happlay.jingsai.model.dto.*;
import online.happlay.jingsai.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.UserQuery;
import online.happlay.jingsai.model.vo.LoginUserVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface IUserService extends IService<User> {

    void register(RegisterDTO registerDto);

    LoginUserVO login(LoginUserDTO loginUserDTO);

    boolean checkLoginUserById(String userId);

    boolean checkUserStatusById(String userId);

    PaginationResultVO<UserVO> selectUser(UserQuery userQuery, HttpServletRequest request);

    void savePassword(PasswordDTO passwordDTO, HttpServletRequest request);

    LoginUserVO saveUser(UserDTO userDTO, HttpServletRequest request);

    void updateStatus(UserStatusDTO userStatusDTO, HttpServletRequest request);

    void deleteUser(Integer id, HttpServletRequest request);

    void updatePassword(UpPWDTO upPWDTO, HttpServletRequest request);

}
