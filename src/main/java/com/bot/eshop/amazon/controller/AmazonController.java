package com.bot.eshop.amazon.controller;

import com.bot.eshop.amazon.model.AmazonSaleProduct;
import com.bot.eshop.amazon.service.AmazonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
public class AmazonController {

    @Autowired
    private AmazonService amazonService;

    @GetMapping("/amazon/products")
    public String scanAmazonProducts() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        amazonService.scanProducts();
        return "DONE";
    }

    @GetMapping("/amazon/sales")
    public List<AmazonSaleProduct> getSales() {
        amazonService.cleanAmazonSaleProductsDB();
        return amazonService.getSales();
    }
}
