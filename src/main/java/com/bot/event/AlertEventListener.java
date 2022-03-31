package com.bot.event;

import com.bot.eshop.amazon.model.AmazonSaleProduct;
import com.bot.eshop.amazon.repository.AmazonSaleProductRepository;
import com.bot.eshop.pccomponentes.model.PcComponentesSaleProduct;
import com.bot.eshop.pccomponentes.repository.PcComponentesSaleProductRepository;
import com.bot.event.alert.email.Email;
import com.bot.event.alert.telegram.PricesCheckerBot;
import com.sendgrid.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AlertEventListener implements ApplicationListener<AlertEvent> {

    @Value("${alert.email:false}")
    private boolean alertEmail;

    @Value("${alert.telegram:false}")
    private boolean alertTelegram;

    @Autowired
    private PcComponentesSaleProductRepository pcComponentesSaleProductRepository;

    @Autowired
    private AmazonSaleProductRepository amazonSaleProductRepository;

    @Autowired
    private Email email;

    @Autowired
    private PricesCheckerBot pricesCheckerBot;

    @SneakyThrows
    @Override
    public void onApplicationEvent(AlertEvent event) {
        log.info("Received event.");

        List<String> saleProductList = getSales(pcComponentesSaleProductRepository.findAll(), amazonSaleProductRepository.findAll());

        if(AlertEvent.areNewSales && !saleProductList.isEmpty()) {
            log.info("There are some new sales.");
            sendAlerts(saleProductList);
            AlertEvent.areNewSales = false;
        } else {
            log.info("There are not any sales.");
        }
    }

    private void sendAlerts(List<String> saleProductList) throws IOException {
        if(alertEmail) {
            Response response = email.sendEmail(saleProductList);
            log.info(System.lineSeparator() + "Email response info:" +
                    System.lineSeparator() + response.getStatusCode() +
                    System.lineSeparator() + response.getBody() +
                    System.lineSeparator() + response.getHeaders());
        }

        if(alertTelegram) {
            pricesCheckerBot.sendSales(saleProductList);
        }
    }

    private List<String> getSales(List<PcComponentesSaleProduct> pcComponentesSaleProductList, List<AmazonSaleProduct> amazonSaleProductList) {
        List<String> listSales = new ArrayList<>();

        for (PcComponentesSaleProduct saleProduct : pcComponentesSaleProductList) {
            listSales.add(saleProduct.toString());
        }

        for (AmazonSaleProduct saleProduct : amazonSaleProductList) {
            listSales.add(saleProduct.toString());
        }

        return listSales;
    }
}
