package com.example.telegram.bot.service;

import com.example.telegram.bot.config.BotConfig;
import com.example.telegram.bot.model.CurrencyModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        CurrencyModel currencyModel = new CurrencyModel();
        String currency = "";

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    try {
                        currency = CurrencyService.getCurrencyRate(messageText, currencyModel);
                    }catch (IOException e){
                        sendMessage(chatId, "Мы не нашли такой валюты");
                    }catch (ParseException e){
                        throw new RuntimeException("Не удалось разобрать дату");
                    }
                    sendMessage(chatId, currency);
            }
        }
    }

    private void startCommandReceived(Long charId, String name){
        String answer = "Привет, " + name + ", приятно познакомиться!" + "\n" +
                "Введите валюту, курс официального обмена" + "\n" +
                "которой вы хотите узнать относительно BYN." + "\n" +
                "Например: USD.";

        sendMessage(charId, answer);
    }

    private void sendMessage(Long charId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(charId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        }catch (TelegramApiException e){

        }
    }


}
