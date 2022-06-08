package com.yushkevich.mms.challenge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {
  @Autowired private MockMvc mvc;
  @MockBean private ProductRepository productRepository;

  @Test
  void testGetById() throws Exception {
    final UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final Product value = new Product(uuid, "name", "a", ProductStatus.ACTIVE, "b", "c");

    when(productRepository.findById(any())).thenReturn(Optional.of(value));

    mvc.perform(get("/products/{uuid}", uuid))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    """
             {"id":"11111111-1111-1111-1111-111111111111","name":"name","relationPath":"a","onlineStatus":"ACTIVE","descriptionLong":"b","descriptionShort":"c"}"""));
  }

  @Test
  void testGetByIdNotFound() throws Exception {
    final UUID uuid = UUID.randomUUID();
    when(productRepository.findById(any())).thenReturn(Optional.empty());

    mvc.perform(get("/products/{uuid}", uuid))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$").value("Product not found by uuid = " + uuid));
  }

  @Test
  void testCreateProduct() throws Exception {
    final UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final Product product = new Product(uuid, "name", "a", ProductStatus.ACTIVE, "b", "c");

    when(productRepository.save(any())).thenReturn(product);

    mvc.perform(
            post("/products")
                .content(
                    """
                           {"name":"name","relationPath":"a","onlineStatus":"ACTIVE","descriptionLong":"b","descriptionShort":"c"}""")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(
            content()
                .string(
                    """
            {"id":"11111111-1111-1111-1111-111111111111","name":"name","relationPath":"a","onlineStatus":"ACTIVE","descriptionLong":"b","descriptionShort":"c"}"""));
  }

  @Test
  void testUpdateProduct() throws Exception {
    final UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    final Product oldProduct = new Product(uuid, "name", "a", ProductStatus.ACTIVE, "b", "c");
    final Product newProduct = new Product(uuid, "name", "a1", ProductStatus.ACTIVE, "b1", "c1");

    when(productRepository.findById(any())).thenReturn(Optional.of(oldProduct));
    when(productRepository.save(any())).thenReturn(newProduct);

    mvc.perform(
            put("/products/{uuid}", uuid)
                .content(
                    """
                             {"name":"name","relationPath":"a1","onlineStatus":"ACTIVE","descriptionLong":"b1","descriptionShort":"c1"}""")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    """
            {"id":"11111111-1111-1111-1111-111111111111","name":"name","relationPath":"a1","onlineStatus":"ACTIVE","descriptionLong":"b1","descriptionShort":"c1"}"""));
  }

  @Test
  void testDeleteProduct() throws Exception {
    mvc.perform(delete("/products/{uuid}", UUID.fromString("11111111-1111-1111-1111-111111111111")))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }
}
