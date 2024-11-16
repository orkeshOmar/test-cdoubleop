package com.dz.coop;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDubbo
public class CoopClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoopClientApplication.class, args);
    }
}
