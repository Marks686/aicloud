package org.spring.aicloud.util;

import cn.hutool.crypto.SecureUtil;
import jakarta.servlet.http.HttpServletRequest;

public class NameUtil {
    /**
     * 获取验证码图片名称
     * @param request
     * @return
     */
    public static String getCaptchaName(HttpServletRequest request) {
        return "captcha-" + SecureUtil.md5(request.getRemoteAddr());
    }
}
