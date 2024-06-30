package org.spring.aicloud.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.simpleframework.xml.core.Validate;
import org.spring.aicloud.entity.User;
import org.spring.aicloud.entity.dto.UserDTO;
import org.spring.aicloud.service.IUserService;
import org.spring.aicloud.util.NameUtil;
import org.spring.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IUserService userService;
    // 登录 login 方法
    @RequestMapping("/login")
    public ResponseEntity login(@Validated UserDTO userDTO, HttpServletRequest request) {

        // 1.校验图片验证码
        String redisCaptchaKey = NameUtil.getCaptchaName(request);
        String redisCaptcha = (String) redisTemplate.opsForValue().get(redisCaptchaKey);
        if (!StringUtils.hasLength(redisCaptcha) ||
                !redisCaptcha.equalsIgnoreCase(userDTO.getCaptcha())) {
            return ResponseEntity.fail("输入图片验证码错误");
        }

        // 3.校验用户名密码
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        User user = userService.getOne(queryWrapper);
        if (user != null && user.getPassword().equals(userDTO.getPassword())) {
            // 4.登录成功
            return ResponseEntity.succ("登录成功");
        }
        return ResponseEntity.fail("用户名或密码错误");
    }


    /**
     * 添加用户
     */

    @RequestMapping("/add")
    public ResponseEntity add(@Validated User user) {
        boolean result = userService.save(user);
        return ResponseEntity.succ("result: " + result);
    }


}
