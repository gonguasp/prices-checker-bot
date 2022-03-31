package com.bot.service;

import com.bot.eshop.amazon.model.AmazonSaleProduct;
import com.bot.eshop.amazon.repository.AmazonSaleProductRepository;
import com.bot.eshop.pccomponentes.model.PcComponentesSaleProduct;
import com.bot.eshop.pccomponentes.repository.PcComponentesSaleProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    @Autowired
    private PcComponentesSaleProductRepository pcComponentesSaleProductRepository;

    @Autowired
    private AmazonSaleProductRepository amazonSaleProductRepository;

    public List<String> getSales() {
        List<String> listSales = new ArrayList<>();

        for (PcComponentesSaleProduct saleProduct : pcComponentesSaleProductRepository.findAll()) {
            listSales.add(saleProduct.toString());
        }

        for (AmazonSaleProduct saleProduct : amazonSaleProductRepository.findAll()) {
            listSales.add(saleProduct.toString());
        }

        return listSales;
    }
}
