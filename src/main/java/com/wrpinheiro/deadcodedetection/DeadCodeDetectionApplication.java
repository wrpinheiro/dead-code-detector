package com.wrpinheiro.deadcodedetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Application entrypoint.
 */
@SpringBootApplication
@EnableAsync
public class DeadCodeDetectionApplication {
    public static void main(final String[] args) {
        SpringApplication.run(DeadCodeDetectionApplication.class, args);
    }
}
