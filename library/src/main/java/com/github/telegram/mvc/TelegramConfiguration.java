package com.github.telegram.mvc;

import com.github.telegram.mvc.config.TelegramBotBuilder;
import com.github.telegram.mvc.config.TelegramMvcConfiguration;
import com.github.telegram.mvc.config.TelegramScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Конфигурирование бинов
 */
@Configuration
public class TelegramConfiguration implements BeanFactoryPostProcessor {

    @Bean
    RequestDispatcher requestDispatcher(List<TelegramMvcConfiguration> telegramMvcConfigurations){

        for (TelegramMvcConfiguration telegramMvcConfiguration : telegramMvcConfigurations) {
            TelegramBotBuilder telegramBotBuilder = new TelegramBotBuilder();
            telegramMvcConfiguration.configuration(telegramBotBuilder);

            //Получим список ботов

        }
        return null;
    }

    @Bean
    HandlerMethodContainer handlerMethodContainer(){
       return new HandlerMethodContainer();
    }

    @Bean
    TelegramControllerBeanPostProcessor telegramControllerBeanPostProcessor() {
        return new TelegramControllerBeanPostProcessor(handlerMethodContainer());
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(TelegramScope.SCOPE, new TelegramScope(beanFactory, TimeUnit.HOURS.toMillis(1)));
    }
}
