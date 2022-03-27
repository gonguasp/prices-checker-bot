package com.bot.eshop.amazon.model;

import com.bot.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class AmazonProduct extends Product {

    public AmazonProduct(String name, double price, String href) {
        this.name = name;
        this.price = price;
        this.href = href;
    }

    @Override
    public String toString() {
        return System.lineSeparator() + this.name +
                System.lineSeparator() + this.price + " €";
    }
}
