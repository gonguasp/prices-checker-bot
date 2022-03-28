package com.bot.eshop.pccomponentes.repository;

import com.bot.eshop.pccomponentes.model.PcComponentesSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PcComponentesSaleProductRepository extends JpaRepository<PcComponentesSaleProduct, Long> {

    Optional<PcComponentesSaleProduct> findByName(String name);

    void removeById(long id);
}
