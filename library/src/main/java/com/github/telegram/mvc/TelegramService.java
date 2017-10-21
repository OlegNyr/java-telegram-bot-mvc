package com.github.telegram.mvc;


import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);
    private final TelegramBot telegramBot;
    private final RequestDispatcher botRequestDispatcher;

    public TelegramService(TelegramBot telegramBot, RequestDispatcher botRequestDispatcher) {
        this.telegramBot = telegramBot;
        this.botRequestDispatcher = botRequestDispatcher;
    }
}
