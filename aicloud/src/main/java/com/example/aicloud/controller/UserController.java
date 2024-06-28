package com.example.aicloud.controller;

import com.example.aicloud.util.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    // 登录 login 方法
    @RequestMapping("/login")
    public ResponseEntity login(String username, String password) {
        // 1.非空判断
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return ResponseEntity.fail("用户名或密码不能为空");
        }
        if ("admin".equals(username) && "admin".equals(password)) {
            return ResponseEntity.succ("登录成功");
        }
        return ResponseEntity.fail("用户名或密码错误");
    }
}
