package com.mitwpu.cseconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CseConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CseConnectApplication.class, args);
    }
}
