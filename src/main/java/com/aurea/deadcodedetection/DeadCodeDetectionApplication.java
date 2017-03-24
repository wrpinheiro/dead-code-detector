package com.aurea.deadcodedetection;

import com.aurea.deadcodedetection.dao.RepositoryDAO;
import com.aurea.deadcodedetection.model.AnalysisStatus;
import com.aurea.deadcodedetection.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class DeadCodeDetectionApplication {

	@Autowired
	private RepositoryDAO repositoryDAO;

	@PostConstruct
	public void init() {
		repositoryDAO.save(Repository.builder()
			.repositoryName("a name").repositoryUrl("a url").createdAt(new Date())
			.status(AnalysisStatus.ADDED).build());
		repositoryDAO.save(Repository.builder()
				.repositoryName("a name").repositoryUrl("a url").createdAt(new Date())
				.status(AnalysisStatus.COMPLETED).build());
	}

	public static void main(String[] args) {
		SpringApplication.run(DeadCodeDetectionApplication.class, args);
	}
}
