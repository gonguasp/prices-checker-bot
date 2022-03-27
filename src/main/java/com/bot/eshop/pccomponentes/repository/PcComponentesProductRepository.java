package com.bot.eshop.pccomponentes.repository;

import com.bot.eshop.pccomponentes.model.PcComponentesProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PcComponentesProductRepository extends JpaRepository<PcComponentesProduct, Long> {

    Optional<PcComponentesProduct> findByName(String name);
}
