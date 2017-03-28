package com.wrpinheiro.deadcodedetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DeadCodeDetectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeadCodeDetectionApplication.class, args);
	}
}
