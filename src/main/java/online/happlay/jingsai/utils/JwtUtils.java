package online.happlay.jingsai.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.entity.User;
import online.happlay.jingsai.model.vo.LoginUserVO;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    /**
     * 创建Token
     *
     * @param userId   用户id
     * @param userRole 用户角色
     * @return Token
     */
    public String createToken(String userId, String userRole) {
        return JWT.create()
                .setPayload("userId", userId)
                .setPayload("role", userRole)
                .setSigner(JWTSignerUtil.none())
                .sign();
    }

    public String getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        verifyToken(token);
        JWT jwt = JWT.of(token);
        return (String) jwt.getPayload("userId");
    }

    public String getUserRole(HttpServletRequest request) {
        String token = request.getHeader("token");
        verifyToken(token);
        JWT jwt = JWT.of(token);
        return (String) jwt.getPayload("role");
    }

    public void verifyToken(String token) {
        boolean verify = JWT.of(token).verify();
        ThrowUtils.throwIf(!verify, ErrorCode.TOKEN_ERROR);
    }

}
