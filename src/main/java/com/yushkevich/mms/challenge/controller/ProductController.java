package com.yushkevich.mms.challenge.controller;

import com.yushkevich.mms.challenge.model.Product;
import com.yushkevich.mms.challenge.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.flogger.Flogger;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@AllArgsConstructor
public class ProductController {
  private final ProductRepository productRepository;

  @GetMapping("/products/{uuid}")
  Product one(@PathVariable UUID uuid) {
    log.debug("GET by uuid = '{}'", uuid);

    return productRepository
        .findById(uuid)
        .orElseThrow(() -> new IllegalArgumentException("Not found"));
  }
}
