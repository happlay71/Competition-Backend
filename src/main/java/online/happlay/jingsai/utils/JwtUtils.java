package online.happlay.jingsai.utils;


import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.ThrowUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class JwtUtils {

    public String createToken(String userId, String userRole) {
        return JWT.create()
                .setPayload("userId", userId)
                .setPayload("userRole", userRole)
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
        return (String) jwt.getPayload("userRole");
    }

    public void verifyToken(String token) {
        boolean verify = JWT.of(token).verify();
        ThrowUtils.throwIf(!verify, ErrorCode.TOKEN_ERROR);
    }
}
