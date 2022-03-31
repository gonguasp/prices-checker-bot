package com.bot.event.alert.email;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class Email {

    private String email;

    private String subject;

    private String token;

    public Response sendEmail(List<String> saleProductList) throws IOException {
        com.sendgrid.helpers.mail.objects.Email fromTo = new com.sendgrid.helpers.mail.objects.Email(email);
        Content content = new Content("text/html", formatEmailContent(saleProductList));
        Mail mail = new Mail(fromTo, subject, fromTo, content);

        SendGrid sg = new SendGrid(token);
        Request request = new Request();
        request.setMethod(com.sendgrid.Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        return sg.api(request);
    }

    private String formatEmailContent(List<String> saleProductList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String saleProductText : saleProductList) {
            stringBuilder.append("<p>");
            stringBuilder.append(saleProductText);
            stringBuilder.append("</p>");
        }

        return stringBuilder.toString();
    }
}
