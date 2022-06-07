package com.yushkevich.mms.challenge.controller;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.model.Product;
import com.yushkevich.mms.challenge.repository.CategoryRepository;
import com.yushkevich.mms.challenge.repository.ProductRepository;
import com.yushkevich.mms.challenge.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
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
}
