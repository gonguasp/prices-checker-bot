package com.bot.eshop.amazon.repository;

import com.bot.eshop.amazon.model.AmazonSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface AmazonSaleProductRepository extends JpaRepository<AmazonSaleProduct, Long> {

    Optional<AmazonSaleProduct> findByName(String name);

    void removeAllByCreatedBefore(Instant weekAgo);
}
