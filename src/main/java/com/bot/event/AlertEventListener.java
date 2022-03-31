package com.bot.event;

import com.bot.event.alert.email.Email;
import com.bot.event.alert.telegram.PricesCheckerBot;
import com.bot.service.SaleService;
import com.sendgrid.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class AlertEventListener implements ApplicationListener<AlertEvent> {

    @Value("${alert.email:false}")
    private boolean alertEmail;

    @Value("${alert.telegram:false}")
    private boolean alertTelegram;

    @Autowired
    private SaleService saleService;

    @Autowired
    private Email email;

    @Autowired
    private PricesCheckerBot pricesCheckerBot;

    @SneakyThrows
    @Override
    public void onApplicationEvent(AlertEvent event) {
        log.info("Received event.");

        List<String> saleProductList = saleService.getSales();

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
}
