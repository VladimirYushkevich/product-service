package com.yushkevich.mms.challenge.service;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.function.UnaryOperator.identity;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public Future<Optional<CategoryDTO>> findCategory(Long categoryId, String breadcrumbSeparator) {
    return CompletableFuture.supplyAsync(() -> categoryRepository.findById(categoryId))
        .thenCombine(
            CompletableFuture.supplyAsync(() -> buildBreadcrumb(categoryId, breadcrumbSeparator)),
            (category, breadcrumb) -> category.map(c -> toCategoryDTO(c, breadcrumb)));
  }

  public List<CategoryDTO> findAllCategories() {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
        .parallel()
        .map(c -> toCategoryDTO(c, buildBreadcrumb(c.getId(), "/")))
        .collect(Collectors.toList());
  }

  private CategoryDTO toCategoryDTO(Category category, String breadcrumb) {
    return new CategoryDTO(
        category.getId(), category.getName(), category.getParentId(), breadcrumb);
  }

  private String buildBreadcrumb(Long categoryId, String separator) {
    final Map<Long, Category> categories = getAllCategories();
    final Deque<String> breadcrumb = new LinkedList<>();

    return String.join(separator, buildBreadcrumb(categories, categoryId, breadcrumb));
  }

  private Deque<String> buildBreadcrumb(
      Map<Long, Category> categories, Long categoryId, Deque<String> breadcrumb) {
    Optional<Category> maybeCategory = Optional.ofNullable(categories.get(categoryId));
    if (maybeCategory.isEmpty()) {
      return breadcrumb;
    } else {
      breadcrumb.addFirst(maybeCategory.get().getName());
    }

    return buildBreadcrumb(categories, maybeCategory.get().getParentId(), breadcrumb);
  }

  private Map<Long, Category> getAllCategories() {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
        .collect(Collectors.toMap(Category::getId, identity()));
  }
}
