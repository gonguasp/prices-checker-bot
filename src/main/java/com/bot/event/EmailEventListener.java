package com.bot.event;

import com.bot.eshop.amazon.model.AmazonSaleProduct;
import com.bot.eshop.amazon.repository.AmazonSaleProductRepository;
import com.bot.eshop.pccomponentes.model.PcComponentesSaleProduct;
import com.bot.eshop.pccomponentes.repository.PcComponentesSaleProductRepository;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class EmailEventListener implements ApplicationListener<EmailEvent> {

    @Autowired
    private PcComponentesSaleProductRepository pcComponentesSaleProductRepository;

    @Autowired
    private AmazonSaleProductRepository amazonSaleProductRepository;

    @SneakyThrows
    @Override
    public void onApplicationEvent(EmailEvent event) {
        log.info("Received email event.");

        List<PcComponentesSaleProduct> pcComponentesSaleProductList = pcComponentesSaleProductRepository.findAll();
        List<AmazonSaleProduct> amazonSaleProductList = amazonSaleProductRepository.findAll();

        if(!pcComponentesSaleProductList.isEmpty() || !amazonSaleProductList.isEmpty()) {
            if(EmailEvent.areNewSales) {
                log.info("There are some new sales. Sending email.");
                sendEmail(formatEmailContent(
                        pcComponentesSaleProductList,
                        amazonSaleProductList));
            } else {
                log.info("There are not any new sales.");
            }
        } else {
            log.info("There are not any sales.");
        }
    }

    private void sendEmail(String contentTextPlain) throws IOException {
        Email fromTo = new Email(replaceMeWithYourEmail);
        String subject = "Alert sale product/s";
        Content content = new Content("text/html", contentTextPlain);
        Mail mail = new Mail(fromTo, subject, fromTo, content);

        SendGrid sg = new SendGrid(replaceMeWithYourApi);
        Request request = new Request();
        request.setMethod(com.sendgrid.Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        log.info(System.lineSeparator() + "Email response info:" +
                System.lineSeparator() + response.getStatusCode() +
                System.lineSeparator() + response.getBody() +
                System.lineSeparator() + response.getHeaders());

        EmailEvent.areNewSales = false;
    }

    private String formatEmailContent(List<PcComponentesSaleProduct> pcComponentesSaleProductList, List<AmazonSaleProduct> amazonSaleProductList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (PcComponentesSaleProduct saleProduct : pcComponentesSaleProductList) {
            stringBuilder.append(saleProduct.toStringHtml());
        }

        for (AmazonSaleProduct saleProduct : amazonSaleProductList) {
            stringBuilder.append(saleProduct.toStringHtml());
        }

        return stringBuilder.toString();
    }
}
