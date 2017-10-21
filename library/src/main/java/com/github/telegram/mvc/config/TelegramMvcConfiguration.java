package com.github.telegram.mvc.config;

/**
 * Интерфейс конфигурации бота
 */
public interface TelegramMvcConfiguration {
    void configuration(TelegramBotBuilder telegramBotBuilder);
}
