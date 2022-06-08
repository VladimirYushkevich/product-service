package com.yushkevich.mms.challenge.controller;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = CategoryController.class)
public class CategoryControllerTest {
  @Autowired private MockMvc mvc;
  @MockBean private CategoryService categoryService;

  @Test
  void testGetById() throws Exception {
    when(categoryService.findCategory(any(), any()))
        .thenReturn(
            CompletableFuture.supplyAsync(
                () -> Optional.of(new CategoryDTO(1L, "name", null, "breadcrumb"))));

    mvc.perform(get("/categories/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("name"))
        .andExpect(jsonPath("$.parentId").doesNotExist())
        .andExpect(jsonPath("$.breadcrumb").value("breadcrumb"));
  }

  @Test
  void testGetByIdNotFound() throws Exception {
    when(categoryService.findCategory(any(), any()))
        .thenReturn(CompletableFuture.supplyAsync(Optional::empty));

    mvc.perform(get("/categories/{id}", 1L))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$").value("Category not found by id = " + 1L));
  }

  @Test
  void testGetByIdInterrupted() throws Exception {
    doAnswer(
            invocation -> {
              throw new InterruptedException("InterruptedException");
            })
        .when(categoryService)
        .findCategory(any(), any());

    mvc.perform(get("/categories/{id}", 1L))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$").value("Category founding interrupted by id = " + 1L));
  }

  @Test
  void testGetByIdExecutionException() throws Exception {
    doAnswer(
            invocation -> {
              throw new ExecutionException(new RuntimeException("InterruptedException"));
            })
        .when(categoryService)
        .findCategory(any(), any());

    mvc.perform(get("/categories/{id}", 1L))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$").value("Category founding interrupted by id = " + 1L));
  }
}
