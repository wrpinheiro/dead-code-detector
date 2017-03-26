package com.wrpinheiro.deadcodedetection;

import com.wrpinheiro.deadcodedetection.dao.RepositoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
public class DeadCodeDetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeadCodeDetectionApplication.class, args);
	}
}
