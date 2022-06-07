package com.yushkevich.mms.challenge.controller;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@AllArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping("/categories/{id}")
  CategoryDTO one(@PathVariable Long id) {
    log.debug("GET by id = '{}'", id);
    try {
      return categoryService
          .findCategory(id, "/")
          .get()
          .orElseThrow(() -> new RuntimeException("Not found"));
    } catch (InterruptedException | ExecutionException e) {
      log.error("Failed to find category");
      throw new RuntimeException("Interrupted");
    }
  }

  @GetMapping("/categories")
  List<CategoryDTO> all() {
    log.debug("GET all categories");
    return categoryService.findAllCategories();
  }

  @PostMapping("/categories")
  CategoryDTO newCategory(@RequestBody Category newCategory) {
    log.debug("POST new category {}", newCategory);
    return categoryService.saveCategory(newCategory);
  }

  @PutMapping("/categories/{id}")
  CategoryDTO replaceCategory(@RequestBody Category newCategory, @PathVariable Long id) {
    log.debug("PUT new category {} for id = {}", newCategory, id);
    return categoryService.replaceCategory(id, newCategory);
  }

  @DeleteMapping("/categories/{id}")
  void deleteCategory(@PathVariable Long id) {
    log.debug("DELETE category by id = {}", id);
    categoryService.deleteCategory(id);
  }
}
