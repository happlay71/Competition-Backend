package online.happlay.jingsai.aop;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import online.happlay.jingsai.annotation.AuthCheck;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.enums.UserRoleEnum;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.vo.LoginUserVO;
import online.happlay.jingsai.service.IUserService;
import online.happlay.jingsai.utils.JwtUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class AuthInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private IUserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 必须得登录才能通过
        if (authCheck.checkLogin() || StrUtil.isNotBlank(mustRole)) {
            String userId = jwtUtils.getUserId(request);
            ThrowUtils.throwIf(userService.checkLoginUserById(userId), ErrorCode.NOT_LOGIN_ERROR);
        }
        // 必须有该权限才能通过
        if (StrUtil.isNotBlank(mustRole)) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = jwtUtils.getUserRole(request);
            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    log.error("权限不足");
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
        return joinPoint.proceed();
    }
}