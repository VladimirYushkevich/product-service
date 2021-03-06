package com.yushkevich.mms.challenge.service;

import static java.util.function.UnaryOperator.identity;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.model.Category;
import com.yushkevich.mms.challenge.repository.CategoryRepository;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryService {
  private static final String BREADCRUMB_SEPARATOR = "/";
  private final CategoryRepository categoryRepository;

  @Transactional
  public Future<Optional<CategoryDTO>> findCategory(Long categoryId, String breadcrumbSeparator) {
    return CompletableFuture.supplyAsync(() -> categoryRepository.findById(categoryId))
        .thenCombine(
            CompletableFuture.supplyAsync(() -> buildBreadcrumb(categoryId, breadcrumbSeparator)),
            (category, breadcrumb) -> category.map(c -> toCategoryDTO(c, breadcrumb)));
  }

  @Transactional
  public List<CategoryDTO> findAllCategories() {
    return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
        .parallel()
        .map(c -> toCategoryDTO(c, buildBreadcrumb(c.getId(), BREADCRUMB_SEPARATOR)))
        .collect(Collectors.toList());
  }

  @Transactional
  public CategoryDTO saveCategory(Category category) {
    final Category savedCategory = categoryRepository.save(category);
    return toCategoryDTO(
        savedCategory, buildBreadcrumb(savedCategory.getId(), BREADCRUMB_SEPARATOR));
  }

  @Transactional
  public CategoryDTO replaceCategory(Long id, Category newCategory) {
    return categoryRepository
        .findById(id)
        .map(oldCategory -> updateCategory(newCategory, oldCategory))
        .orElseGet(() -> updateCategory(newCategory, new Category(id, null, null)));
  }

  @Transactional
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }

  private CategoryDTO updateCategory(Category newCategory, Category oldCategory) {
    // validation for cycling dependency
    final Long parentId = newCategory.getParentId();
    if (parentId != null) {
      final Set<Long> parents = buildParents(getAllCategories(), parentId, new HashSet<>());
      if (parents.contains(oldCategory.getId())) {
        throw new IllegalArgumentException("Cyclic dependency found");
      }
    }
    oldCategory.setName(newCategory.getName());
    oldCategory.setParentId(newCategory.getParentId());
    return saveCategory(oldCategory);
  }

  private CategoryDTO toCategoryDTO(Category category, String breadcrumb) {
    return new CategoryDTO(
        category.getId(), category.getName(), category.getParentId(), breadcrumb);
  }

  private String buildBreadcrumb(Long categoryId, String separator) {
    return String.join(
        separator, buildBreadcrumb(getAllCategories(), categoryId, new LinkedList<>()));
  }

  private Set<Long> buildParents(
      Map<Long, Category> categories, Long categoryId, Set<Long> breadcrumb) {
    final Optional<Category> maybeCategory = Optional.ofNullable(categories.get(categoryId));
    if (maybeCategory.isEmpty()) {
      return breadcrumb;
    } else {
      breadcrumb.add(maybeCategory.get().getId());
    }

    return buildParents(categories, maybeCategory.get().getParentId(), breadcrumb);
  }

  private Deque<String> buildBreadcrumb(
      Map<Long, Category> categories, Long categoryId, Deque<String> breadcrumb) {
    final Optional<Category> maybeCategory = Optional.ofNullable(categories.get(categoryId));
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
