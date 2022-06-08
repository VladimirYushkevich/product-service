package com.yushkevich.mms.challenge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.repository.CategoryRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test-containers-flyway")
public class CategoryServiceIntegrationTests {

  @Autowired private CategoryService categoryService;
  @Autowired private CategoryRepository categoryRepository;

  @Test
  void shouldFindById() throws Exception {
    assertEquals(
        Optional.of(new CategoryDTO(202L, "TV & Audio", 201L, "MediaMarkt_DE|TV & Audio")),
        categoryService.findCategory(202L, "|").get());
  }

  @Test
  void shouldFindAll() {
    assertFalse(categoryService.findAllCategories().isEmpty());
  }

  @Test
  void shouldCreateNewCategoryAndDelete() {
    final CategoryDTO savedCategory =
        categoryService.saveCategory(new Category(null, "IT new", 202L));
    assertEquals(
        new CategoryDTO(savedCategory.id(), "IT new", 202L, "MediaMarkt_DE/TV & Audio/IT new"),
        savedCategory);
    assertEquals(
        Optional.of(new Category(savedCategory.id(), "IT new", 202L)),
        categoryRepository.findById(savedCategory.id()));

    categoryService.deleteCategory(savedCategory.id());

    assertEquals(Optional.empty(), categoryRepository.findById(savedCategory.id()));
  }

  @Test
  void shouldUpdateNewCategory() {
    final long maxId = findMaxId();
    final CategoryDTO replacedCategory =
        categoryService.replaceCategory(maxId + 1, new Category(maxId + 1, "IT new3", 202L));
    assertEquals(
        new CategoryDTO(replacedCategory.id(), "IT new3", 202L, "MediaMarkt_DE/TV & Audio/IT new3"),
        replacedCategory);
  }

  @Test
  void shouldUpdateExistingCategory() {
    assertEquals(
        new CategoryDTO(203L, "IT new4", 202L, "MediaMarkt_DE/TV & Audio/IT new4"),
        categoryService.replaceCategory(203L, new Category(203L, "IT new4", 202L)));
  }

  private long findMaxId() {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
        .map(Category::getId)
        .mapToLong(v -> v)
        .max()
        .orElseThrow(NoSuchElementException::new);
  }
}
