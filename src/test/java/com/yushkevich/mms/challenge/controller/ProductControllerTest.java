package com.yushkevich.mms.challenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yushkevich.mms.challenge.model.Product;
import com.yushkevich.mms.challenge.model.ProductStatus;
import com.yushkevich.mms.challenge.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {
  @Autowired private MockMvc mvc;
  @MockBean private ProductRepository productRepository;

  @Test
  void testGetById() throws Exception {
    final UUID uuid = UUID.randomUUID();
    when(productRepository.findById(any()))
        .thenReturn(
            Optional.of(
                new Product(
                    uuid,
                    "name",
                    "relation_path",
                    ProductStatus.ACTIVE,
                    "description_long",
                    "description_short")));

    mvc.perform(get("/products/{uuid}", uuid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(uuid.toString()))
        .andExpect(jsonPath("$.name").value("name"))
        .andExpect(jsonPath("$.relationPath").value("relation_path"))
        .andExpect(jsonPath("$.onlineStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.descriptionLong").value("description_long"))
        .andExpect(jsonPath("$.descriptionShort").value("description_short"));
  }

  @Test
  void testGetByIdNotFound() throws Exception {
    final UUID uuid = UUID.randomUUID();
    when(productRepository.findById(any())).thenReturn(Optional.empty());

    mvc.perform(get("/products/{uuid}", uuid))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$").value("Product not found by uuid = " + uuid));
  }
}
