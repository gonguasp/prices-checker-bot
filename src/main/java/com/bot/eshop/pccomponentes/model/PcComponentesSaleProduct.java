package com.bot.eshop.pccomponentes.model;

import com.bot.model.SaleProduct;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class PcComponentesSaleProduct extends SaleProduct {

    @NotNull
    private String category;
    @NotNull
    private String brand;
    @NotNull
    private boolean focus;

    public PcComponentesSaleProduct(String name, double currentPrice, double discount, String brand, String category, String href, boolean focus) {
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.currentPrice = currentPrice;
        this.discount = discount;
        this.href = href;
        this.focus = focus;
    }

    public PcComponentesSaleProduct(PcComponentesProduct pcComponentesProduct, double currentPrice, String href, double discount) {
        this(
                pcComponentesProduct.getName(),
                currentPrice,
                discount,
                pcComponentesProduct.getBrand(),
                pcComponentesProduct.getCategory(),
                href,
                pcComponentesProduct.isFocus()
        );
    }
}
