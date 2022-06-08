package com.yushkevich.mms.challenge.repository;

import com.yushkevich.mms.challenge.model.Product;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, UUID> {}
