package com.aurea.deadcodedetection.repository;

import com.aurea.deadcodedetection.model.Repository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by wrpinheiro on 3/21/17.
 */
public interface RepositoryRepository extends CrudRepository<Repository, Long> {
}
