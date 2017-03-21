package com.aurea.assignment.repository;

import com.aurea.assignment.model.Repository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by wrpinheiro on 3/21/17.
 */
public interface RepositoryRepository extends CrudRepository<Repository, Long> {
}
