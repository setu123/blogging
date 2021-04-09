package com.square.blogging.repository;

import com.square.blogging.model.Blog;
import org.springframework.data.repository.CrudRepository;

public interface BlogRepository extends CrudRepository<Blog, Long> {
}
