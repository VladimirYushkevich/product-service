package com.yushkevich.mms.challenge.repository;

import com.yushkevich.mms.challenge.model.Category;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {

  @Override
  @Cacheable("categories")
  Iterable<Category> findAll();
}
