package com.yushkevich.mms.challenge.controller;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.exception.CustomInterruptedException;
import com.yushkevich.mms.challenge.exception.NotFoundException;
import com.yushkevich.mms.challenge.exception.NotValidRequestException;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.service.CategoryService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
class CategoryController {
  private final CategoryService categoryService;

  @GetMapping("/categories/{id}")
  CategoryDTO one(@PathVariable Long id) {
    log.debug("GET by id = '{}'", id);
    try {
      return categoryService
          .findCategory(id, "/")
          .get()
          .orElseThrow(
              () -> new NotFoundException(String.format("Category not found by id = %s", id)));
    } catch (InterruptedException | ExecutionException ex) {
      log.error("Failed to find category by id {}", id, ex);
      throw new CustomInterruptedException(
          String.format("Category founding interrupted by id = %s", id));
    }
  }

  @GetMapping("/categories")
  List<CategoryDTO> all() {
    log.debug("GET all categories");
    return categoryService.findAllCategories();
  }

  @PostMapping("/categories")
  @ResponseStatus(HttpStatus.CREATED)
  CategoryDTO newCategory(@RequestBody Category newCategory) {
    log.debug("POST new category {}", newCategory);
    return categoryService.saveCategory(newCategory);
  }

  @PutMapping("/categories/{id}")
  CategoryDTO replaceCategory(@RequestBody Category newCategory, @PathVariable Long id) {
    log.debug("PUT new category {} for id = {}", newCategory, id);
    try {
      return categoryService.replaceCategory(id, newCategory);
    } catch (IllegalArgumentException ex) {
      log.error("Failed to update category {} by id {}", newCategory, id, ex);
      throw new NotValidRequestException(
          String.format(
              "Failed to update category %s by id %s. Probably cyclic dependency found.",
              newCategory, id));
    }
  }

  @DeleteMapping("/categories/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteCategory(@PathVariable Long id) {
    log.debug("DELETE category by id = {}", id);
    categoryService.deleteCategory(id);
  }
}
