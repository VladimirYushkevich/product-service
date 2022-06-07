package com.yushkevich.mms.challenge.repository;

import com.yushkevich.mms.challenge.model.Category;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface CategoryRepository extends CrudRepository<Category, Long> {

  @Override
  @NonNull
  @Cacheable("categories")
  Iterable<Category> findAll();

  @Override
  @NonNull
  @CacheEvict(value = "categories", allEntries = true)
  <S extends Category> S save(@NonNull S entity);
}
