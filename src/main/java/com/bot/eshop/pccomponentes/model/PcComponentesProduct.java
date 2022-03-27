package com.bot.eshop.pccomponentes.model;

import com.bot.model.Product;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class PcComponentesProduct extends Product {


    @NotNull
    private String category;
    @NotNull
    private String brand;
    @NotNull
    private boolean focus;

    public PcComponentesProduct(String name, double price, String brand, String category, String href, boolean focus) {
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.href = href;
        this.focus = focus;
    }

    @Override
    public String toString() {
        return System.lineSeparator() + this.category +
                System.lineSeparator() + this.brand +
                System.lineSeparator() + this.name +
                System.lineSeparator() + this.price + " €";
    }
}
