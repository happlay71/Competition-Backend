package online.happlay.jingsai.utils;


import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StringUtils {

    public static boolean isNumber(String str) {
        String checkNumber = "^[0-9]+$";
        if (null == str) {
            return false;
        }
        if (!str.matches(checkNumber)) {
            return false;
        }

        return true;
    }

    // BCrypt 加密
    public String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }

        // 使用 BCryptPasswordEncoder 加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(rawPassword);  // 返回加密后的密码
    }

    // 密码匹配验证
    public boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encryptedPassword);  // 验证密码是否匹配
    }

}