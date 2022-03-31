package com.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@MappedSuperclass
public class SaleProduct extends RepresentationModel<SaleProduct> {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Column(unique=true)
    protected String name;
    @NotNull
    protected double currentPrice;
    @NotNull
    protected double discount;
    @NotNull
    @JsonIgnore
    @Column(length = 2000)
    protected String href;
    @NotNull
    @JsonIgnore
    protected Instant created = Instant.now();

    public SaleProduct(String name, double currentPrice, double discount, String href) {
        this.name = name;
        this.currentPrice = currentPrice;
        this.discount = discount;
        this.href = href;
    }

    public SaleProduct(Product product, double currentPrice, String href, double discount) {
        this(
                product.getName(),
                currentPrice,
                discount,
                href
        );
    }

    public String formatToString() {
        return System.lineSeparator() + this.name +
                System.lineSeparator() + this.currentPrice + " €" +
                System.lineSeparator() + this.discount + " €";
    }
}
