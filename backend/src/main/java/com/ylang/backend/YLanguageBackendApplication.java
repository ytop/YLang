package com.ylang.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ylang.backend")
public class YLanguageBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YLanguageBackendApplication.class, args);
    }
}