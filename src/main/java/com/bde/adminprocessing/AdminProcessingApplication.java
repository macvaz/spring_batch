package com.bde.adminprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdminProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminProcessingApplication.class, args);
    }
}
