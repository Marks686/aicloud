package org.spring.aicloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "org.spring.aicloud.mapper")
public class AicloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(AicloudApplication.class, args);
    }

}
