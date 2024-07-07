package org.spring.aicloud.util;

import org.spring.aicloud.entity.SecurityUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    /**
     * 获取当前登录用户
     * @return
     */
    public static SecurityUserDetails getCurrentUser() {
        return (SecurityUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
