package com.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;

@Data
@NoArgsConstructor
@MappedSuperclass
public class Product extends RepresentationModel<SaleProduct> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Column(unique=true)
    protected String name;
    @NotNull
    protected double price;
    @NotNull
    @JsonIgnore
    @Column(length = 2000)
    protected String href;

    @Override
    public String toString() {
        return System.lineSeparator() + this.name +
                System.lineSeparator() + this.price + " €";
    }
}
