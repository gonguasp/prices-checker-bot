package com.bot.event.alert.telegram;

import com.bot.service.IndexService;
import com.bot.service.SaleService;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

    @Autowired
    private SaleService saleService;

    @Autowired
    private IndexService indexService;

    private static final String ACCEPT = "Accept";
    private static final String CANCEL = "Cancel";

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
            switch (update.getMessage().getText()) {
                case "/sales":
                    sendMessage(formatSalesMessage(saleService.getSales()));
                    break;
                case "/scan":
                    indexService.executeAll();
                    sendMessage("Scanning all products done");
                    break;
                case "/reset":
                    askForResetAll();
                    break;
                case "/help":
                    sendMessage("/scan: scan all products" + System.lineSeparator() + System.lineSeparator() +
                            "/sales: get all sales" + System.lineSeparator() + System.lineSeparator() +
                            "/reset: delete all data from database");
                    break;
                default:
                    sendMessage("Command not found!");
                    break;
            }
        } else if(update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if(ACCEPT.equals(data)) {
                indexService.executeAll();
                sendMessage("All data clear");
            } else if(CANCEL.equals(data)) {
                sendMessage("Canceled reset command");
            }

            execute(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
        }
    }

    public void sendSales(List<String> saleProductList) {

        if(!saleProductList.isEmpty()) {
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            String messageContent = formatSalesMessage(saleProductList);
            sendMessage(messageContent);

            Runnable messageSender = () -> {
                Message message = sendMessage(messageContent);
                scheduler.schedule(() -> removeMessage(message), delayCanceler, TimeUnit.MINUTES);
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

    private void askForResetAll() throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(chatId, "This will delete all data in database. Are you sure?");
        InlineKeyboardButton buttonAccept = new InlineKeyboardButton(ACCEPT);
        InlineKeyboardButton buttonCancel = new InlineKeyboardButton(CANCEL);
        buttonAccept.setCallbackData(ACCEPT);
        buttonCancel.setCallbackData(CANCEL);
        List<InlineKeyboardButton> inlineKeyboardButtonList = List.of(buttonAccept, buttonCancel);
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(List.of(inlineKeyboardButtonList)));
        execute(sendMessage);
    }

    @SneakyThrows
    private Message sendMessage(String messageContent) {
        log.info("Sending telegram message");
        return execute(new SendMessage(chatId, messageContent));
    }

    @SneakyThrows
    private void removeMessage(Message message) {
        log.info("Removing telegram message");
        execute(new DeleteMessage(chatId, message.getMessageId()));
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
