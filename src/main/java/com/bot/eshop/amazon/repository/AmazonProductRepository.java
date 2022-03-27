package com.bot.eshop.amazon.repository;

import com.bot.eshop.amazon.model.AmazonProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmazonProductRepository extends JpaRepository<AmazonProduct, Long> {

    Optional<AmazonProduct> findByName(String name);
}
