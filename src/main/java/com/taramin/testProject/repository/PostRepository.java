package com.taramin.testProject.repository;

import com.taramin.testProject.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository <Post,Long> {
}
