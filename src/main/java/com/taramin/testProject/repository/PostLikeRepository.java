package com.taramin.testProject.repository;

import com.taramin.testProject.entity.PostLike;
import org.springframework.data.repository.CrudRepository;

public interface PostLikeRepository extends CrudRepository <PostLike, Long> {
}
