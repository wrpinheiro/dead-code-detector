package com.aurea.deadcodedetection;

import com.aurea.deadcodedetection.dao.RepositoryDAO;
import com.aurea.deadcodedetection.model.AnalysisStatus;
import com.aurea.deadcodedetection.model.CodeSmell;
import com.aurea.deadcodedetection.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.Date;

import static com.aurea.deadcodedetection.model.CodeSmellType.DEAD_CODE;
import static java.util.Arrays.asList;

@SpringBootApplication
@EnableAsync
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
				.status(AnalysisStatus.COMPLETED)
				.codeSmells(asList(
						CodeSmell.builder().filename("fileABC").fromLine(10).toLine(10).codeSmellType(DEAD_CODE).build(),
						CodeSmell.builder().filename("fileXPTO").fromLine(15).toLine(18).codeSmellType(DEAD_CODE).build()))
				.build());
	}

	public static void main(String[] args) {
		SpringApplication.run(DeadCodeDetectionApplication.class, args);
	}
}
