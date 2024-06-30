package com.example.aicloud.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.net.NetUtil;
import com.example.aicloud.util.MinIoUtil;
import com.example.aicloud.util.ResponseEntity;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private MinIoUtil minIoUtil;
    /**
     * 生成验证码
     */
    @RequestMapping("/create")
    public ResponseEntity createCaptcha() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = "";
        // 定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40);
        String fileName = "captcha-" + NetUtil.getLocalhost().toString();
        try(InputStream inputStream = new ByteArrayInputStream(lineCaptcha.getImageBytes())) {
            url =  minIoUtil.upload(fileName, inputStream, "image/png");
        }
        return ResponseEntity.succ(url);
    }
}
