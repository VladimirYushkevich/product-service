package com.yushkevich.mms.challenge.controller;

import com.yushkevich.mms.challenge.exception.NotFoundException;
import com.yushkevich.mms.challenge.model.Product;
import com.yushkevich.mms.challenge.repository.ProductRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
class ProductController {
  private final ProductRepository productRepository;

  @GetMapping("/products/{uuid}")
  Product one(@PathVariable UUID uuid) {
    log.debug("GET by uuid = '{}'", uuid);

    return productRepository
        .findById(uuid)
        .orElseThrow(
            () -> new NotFoundException(String.format("Product not found by uuid = %s", uuid)));
  }

  @PostMapping("/products")
  @ResponseStatus(HttpStatus.CREATED)
  Product newProduct(@RequestBody Product newProduct) {
    log.debug("POST new product {}", newProduct);
    return productRepository.save(newProduct);
  }

  @PutMapping("/products/{uuid}")
  Product replaceProduct(@RequestBody Product newProduct, @PathVariable UUID uuid) {
    log.debug("PUT new product {} for uuid = {}", newProduct, uuid);
    return productRepository
        .findById(uuid)
        .map(p -> productRepository.save(replaceProductFields(p, newProduct)))
        .orElseGet(
            () -> {
              newProduct.setId(uuid);
              return productRepository.save(
                  replaceProductFields(
                      new Product(uuid, null, null, null, null, null), newProduct));
            });
  }

  @DeleteMapping("/products/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteProduct(@PathVariable UUID uuid) {
    log.debug("DELETE product by uuid = {}", uuid);
    productRepository.deleteById(uuid);
  }

  private Product replaceProductFields(Product oldProduct, Product newProduct) {
    oldProduct.setName(newProduct.getName());
    oldProduct.setDescriptionLong(newProduct.getDescriptionLong());
    oldProduct.setDescriptionShort(newProduct.getDescriptionShort());
    oldProduct.setOnlineStatus(newProduct.getOnlineStatus());
    oldProduct.setRelationPath(newProduct.getRelationPath());
    return oldProduct;
  }
}
