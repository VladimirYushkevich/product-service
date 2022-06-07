package com.yushkevich.mms.challenge.repository;

import com.yushkevich.mms.challenge.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {}
