package com.aurea.assignment.controller;

import com.aurea.assignment.model.AnalysisStatus;
import com.aurea.assignment.model.Repository;
import com.aurea.assignment.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@RestController
@RequestMapping("/repository")
public class RepositoryController {

    @Autowired
    private RepositoryRepository repository;

    @RequestMapping("/")
    public Iterable<Repository> getRepositories() {
        return repository.findAll();
    }
}
