package com.bot.eshop.amazon.model;

import com.bot.model.SaleProduct;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class AmazonSaleProduct extends SaleProduct {

    public AmazonSaleProduct(String name, double currentPrice, double discount, String href) {
        this.name = name;
        this.currentPrice = currentPrice;
        this.discount = discount;
        this.href = href;
    }

    public AmazonSaleProduct(AmazonProduct amazonProduct, double currentPrice, String href, double discount) {
        this(
                amazonProduct.getName(),
                currentPrice,
                discount,
                href
        );
    }

    @Override
    public String toString() {
        return this.currentPrice + " € " + this.name + " " + this.href;
    }

    public String toStringHtml() {
        return "<p> - " + this.toString() + "</p>";
    }
}
