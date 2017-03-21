package com.aurea.assignment.controller;

import com.aurea.assignment.model.AnalysisStatus;
import com.aurea.assignment.model.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@RestController
@RequestMapping("/repository")
public class RepositoryController {

    @RequestMapping("/")
    public List<Repository> getRepositories() {
        Repository r = new Repository(1L, AnalysisStatus.ADDED, new Date(), new Date(), "url", "name");
        return Arrays.asList(r);
    }
}
