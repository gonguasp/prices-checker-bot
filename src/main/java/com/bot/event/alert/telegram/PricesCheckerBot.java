package com.bot.event.alert.telegram;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Data
@Service
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
@Slf4j
public class PricesCheckerBot extends TelegramLongPollingBot {

    private String token;

    private String name;

    private String chatId;

    private long initialDelay;

    private long period;

    private long delayCanceler;

    @PostConstruct
    public void registerBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            User senderUser = update.getMessage().getFrom();
            String messageContent = "User " + senderUser.getUserName() + " used the command " + update.getMessage().getText();
            execute(new SendMessage(chatId, messageContent));
        }
    }

    @SneakyThrows
    private void sendMessage(String messageContent) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageContent);
        execute(message);
    }

    public void sendSales(List<String> saleProductList) {
        if(!saleProductList.isEmpty()) {
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            Runnable messageSender = () -> {
                log.info("Sending telegram message");
                sendMessage(formatSalesMessage(saleProductList));
            };
            ScheduledFuture<?> messageSenderHandle =
                    scheduler.scheduleAtFixedRate(messageSender, initialDelay, period, TimeUnit.SECONDS);
            Runnable canceller = () -> {
                log.info("Cancelling telegram messages sending");
                messageSenderHandle.cancel(false);
            };
            scheduler.schedule(canceller, delayCanceler, TimeUnit.MINUTES);
        }
    }

    private String formatSalesMessage(List<String> saleProductList) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("SALE PRODUCTS:" + System.lineSeparator());
        for (String saleProductText : saleProductList) {
            stringBuilder.append(" - ");
            stringBuilder.append(saleProductText);
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }
}
