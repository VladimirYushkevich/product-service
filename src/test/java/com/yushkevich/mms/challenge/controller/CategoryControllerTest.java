package com.yushkevich.mms.challenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.yushkevich.mms.challenge.dto.CategoryDTO;
import com.yushkevich.mms.challenge.service.CategoryService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
        .andExpect(
            content()
                .string(
                    """
            {"id":1,"name":"name","parentId":null,"breadcrumb":"breadcrumb"}"""));
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

  @Test
  void testGetAllWithData() throws Exception {
    when(categoryService.findAllCategories())
        .thenReturn(
            List.of(new CategoryDTO(1L, "a", null, "a"), new CategoryDTO(2L, "b", 1L, "a/b")));

    mvc.perform(get("/categories/"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    """
            [{"id":1,"name":"a","parentId":null,"breadcrumb":"a"},{"id":2,"name":"b","parentId":1,"breadcrumb":"a/b"}]"""));
  }

  @Test
  void testGetAllWithoutData() throws Exception {
    mvc.perform(get("/categories/"))
        .andExpect(status().isOk())
        .andExpect(content().string("""
            []"""));
  }

  @Test
  void testCreateCategory() throws Exception {
    when(categoryService.saveCategory(any())).thenReturn(new CategoryDTO(1L, "a", null, "a"));

    mvc.perform(
            post("/categories")
                .content("""
                            {"name":"a","parentId":null}""")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(
            content()
                .string("""
            {"id":1,"name":"a","parentId":null,"breadcrumb":"a"}"""));
  }

  @Test
  void testUpdateCategory() throws Exception {
    when(categoryService.replaceCategory(any(), any()))
        .thenReturn(new CategoryDTO(1L, "b", null, "b"));

    mvc.perform(
            put("/categories/{id}", 1L)
                .content("""
                                {"id":1,"name":"b","parentId":null}""")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string("""
            {"id":1,"name":"b","parentId":null,"breadcrumb":"b"}"""));
  }

  @Test
  void testUpdateCategoryWithNotValidRequest() throws Exception {
    doAnswer(
            invocation -> {
              throw new IllegalArgumentException("Not valid request");
            })
        .when(categoryService)
        .replaceCategory(any(), any());

    mvc.perform(
            put("/categories/{id}", 1L)
                .content("""
                                {"id":1,"name":"b","parentId":null}""")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(
            content()
                .string(
                    """
            Failed to update category Category(id=1, name=b, parentId=null) by id 1. Probably cyclic dependency found."""));
  }

  @Test
  void testDeleteCategory() throws Exception {
    mvc.perform(delete("/categories/{id}", 1L))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }
}
