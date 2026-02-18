package com.zjjh.fdtemp.common.aspectj;

import com.zjjh.fdtemp.common.core.context.PermissionContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionsAspect {
    @Before("@annotation(preAuthorize)")
    public void doBefore(JoinPoint point, PreAuthorize preAuthorize) throws Throwable {
        handlePreAuthorize(point, preAuthorize);
    }

    protected void handlePreAuthorize(final JoinPoint joinPoint, PreAuthorize preAuthorize) {
        String value = preAuthorize.value();
        // Extract permission string from hasAuthority('xxx') pattern
        if (value != null && value.contains("hasAuthority('")) {
            String permission = value.replace("hasAuthority('", "").replace("')", "");
            PermissionContextHolder.setContext(permission);
        }
    }
}
