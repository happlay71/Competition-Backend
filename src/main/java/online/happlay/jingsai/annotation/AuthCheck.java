package online.happlay.jingsai.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 校验登录
     * @return
     */
    boolean checkLogin() default true;

    /**
     * 必须有某个角色
     *
     * @return
     */
    String mustRole() default "";

}