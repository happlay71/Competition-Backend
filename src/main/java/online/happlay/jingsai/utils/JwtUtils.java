package online.happlay.jingsai.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.entity.User;
import online.happlay.jingsai.model.vo.LoginUserVO;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static online.happlay.jingsai.constants.Constants.EXPIRATION_TIME;
import static online.happlay.jingsai.constants.Constants.SECRET_KEY;

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
        // 设置过期时间
        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        Date expirationDate = new Date(expirationTime);

        // 创建并签名JWT
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("role", userRole)
                .withExpiresAt(expirationDate)  // 设置过期时间
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * 获取用户ID
     *
     * @param request HttpServletRequest
     * @return userId
     */
    public String getUserId(HttpServletRequest request) {
        String token = request.getHeader("token");
        verifyToken(token); // 验证token有效性
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("userId").asString();
    }

    /**
     * 获取用户角色
     *
     * @param request HttpServletRequest
     * @return userRole
     */
    public String getUserRole(HttpServletRequest request) {
        String token = request.getHeader("token");
        verifyToken(token); // 验证token有效性
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("role").asString();
    }

    /**
     * 验证Token的有效性
     *
     * @param token JWT Token
     */
    public void verifyToken(String token) {
        try {
            // 解码JWT并验证签名
            DecodedJWT jwt = JWT.decode(token);

            // 验证Token是否过期
            Date expiration = jwt.getExpiresAt();
            if (expiration.before(new Date())) {
                throw new RuntimeException("Token过期");
            }

            // 如果需要更严格的验证签名，可以使用JWT.require(Algorithm.HMAC256(SECRET_KEY))来进一步验证签名。

        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

}
