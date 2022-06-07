package com.yushkevich.mms.challenge.service;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
  @Mock private CategoryRepository categoryRepository;
  @InjectMocks private CategoryService categoryService;

  @Test
  @DisplayName("Should find category and build breadcrumb with 3 entries.")
  void shouldFindOneAndBuildBreadcrumb() throws Exception {
    Category category = new Category(4L, "c", 2L);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(categoryRepository.findAll())
        .thenReturn(
            List.of(
                new Category(1L, "root", null),
                new Category(2L, "a", 1L),
                new Category(3L, "b", 1L),
                category));

    assertEquals(
        Optional.of(new CategoryDTO(4L, "c", 2L, "root|a|c")),
        categoryService.findCategory(4L, "|").get());
  }

  @Test
  @DisplayName("Should find category and build breadcrumb with 1 entry.")
  void shouldFindOneAndBuildBreadcrumbWithOneEntry() throws Exception {
    Category category = new Category(4L, "c", 3L);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(categoryRepository.findAll())
        .thenReturn(List.of(new Category(1L, "root", null), new Category(2L, "a", 1L), category));

    assertEquals(
        Optional.of(new CategoryDTO(4L, "c", 3L, "c")),
        categoryService.findCategory(4L, "|").get());
  }

  @Test
  @DisplayName("Should find category and build an empty breadcrumb.")
  void shouldFindOneAndBuildEmptyBreadcrumb() throws Exception {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category(5L, "c", 3L)));

    assertEquals(
        Optional.of(new CategoryDTO(5L, "c", 3L, "")), categoryService.findCategory(5L, "|").get());
  }

  @Test
  @DisplayName("Should return empty when no categories found.")
  void shouldReturnEmptyWhenNoCategoriesFound() throws Exception {
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
    when(categoryRepository.findAll()).thenReturn(List.of(new Category(1L, "root", null)));

    assertEquals(Optional.empty(), categoryService.findCategory(1L, "|").get());
  }

  @Test
  @DisplayName("Should build breadcrumb for root entry.")
  void shouldBuildBreadcrumbForRootEntry() throws Exception {
    Category category = new Category(1L, "root", null);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(categoryRepository.findAll()).thenReturn(List.of(category));

    assertEquals(
        Optional.of(new CategoryDTO(1L, "root", null, "root")),
        categoryService.findCategory(1L, "|").get());
  }

  @Test
  @DisplayName("Should find all categories and build breadcrumbs.")
  void shouldFindAllCategoriesAndBuildBreadcrumbs() {
    when(categoryRepository.findAll())
        .thenReturn(
            List.of(
                new Category(1L, "root", null),
                new Category(2L, "a", 1L),
                new Category(3L, "b", 1L),
                new Category(4L, "c", 2L)));

    assertEquals(
        List.of(
            new CategoryDTO(1L, "root", null, "root"),
            new CategoryDTO(2L, "a", 1L, "root/a"),
            new CategoryDTO(3L, "b", 1L, "root/b"),
            new CategoryDTO(4L, "c", 2L, "root/a/c")),
        categoryService.findAllCategories());
  }
}
