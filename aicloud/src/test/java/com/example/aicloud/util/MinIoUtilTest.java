package com.example.aicloud.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MinIoUtilTest {
    @Resource
    private MinIoUtil  minIoUtil;
    @Test
    void upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File("d:\\xiaogou.jpg");
        InputStream inputStream = new FileInputStream(file);
        System.out.println(minIoUtil.upload("xiaogou.jpg",inputStream,"image/jpg"));
    }

    @Test
    void createCaptcha(){
        // 定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40);
        lineCaptcha.write("d:\\captcha.png");
        System.out.println("验证码： " + lineCaptcha.getCode());
    }
}