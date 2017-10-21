package com.github.telegram.sample;

import com.github.telegram.mvc.api.EnableTelegram;
import com.github.telegram.mvc.config.TelegramBotBuilder;
import com.github.telegram.mvc.config.TelegramMvcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
@EnableTelegram
public class SampleTelegramBotMvcMain implements TelegramMvcConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SampleTelegramBotMvcMain.class);
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(SampleTelegramBotMvcMain.class);
    }

    @Override
    public void configuration(TelegramBotBuilder telegramBotBuilder) {
        telegramBotBuilder
                .token(environment.getProperty("telegram.bot.token")).alias("myFirsBean");
    }

}
