package com.taramin.testProject.repository;

import com.taramin.testProject.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository <User, Long> {
}
