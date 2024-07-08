package org.spring.aicloud.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.simpleframework.xml.core.Validate;
import org.spring.aicloud.entity.User;
import org.spring.aicloud.entity.dto.UserDTO;
import org.spring.aicloud.service.IUserService;
import org.spring.aicloud.util.NameUtil;
import org.spring.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IUserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 登录方法
     * @param userDTO
     * @param request
     * @return
     */
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
        if (user != null && passwordEncoder.matches(userDTO.getPassword(),
                user.getPassword())) {
            // 生成 JWT
            HashMap<String, Object> payLoad = new HashMap<>();
            payLoad.put("uid", user.getUid());
            payLoad.put("username", user.getUsername());
            HashMap<String,String> result = new HashMap<>();
            result.put("jwt", JWTUtil.createToken(payLoad, jwtSecret.getBytes()));
            result.put("username", user.getUsername());

            // 4.登录成功
            return ResponseEntity.succ(result);
        }
        return ResponseEntity.fail("用户名或密码错误");
    }

    /**
     * 添加用户
     */

    @RequestMapping("/register")
    public ResponseEntity register(@Validated User user) {
        // 密码进行加盐
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 添加方法
        boolean result = userService.save(user);
        if (result) {
            return ResponseEntity.succ(result);
        }
        return ResponseEntity.fail("未知错误");
    }


    @RequestMapping("/test")
    public String test() {
        return "OK";
    }

}
