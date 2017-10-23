package com.github.telegram.mvc;

import com.github.telegram.mvc.api.TelegramSession;
import com.github.telegram.mvc.config.*;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.pengrad.telegrambot.TelegramBot;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Suppliers.*;

/**
 * Конфигурирование бинов
 */
@Configuration
public class TelegramConfiguration implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(TelegramConfiguration.class);
    private static final String TELEGRAM_BOT_TOKEN = "telegram.bot.token";
    private Environment environment;
    private ConfigurableListableBeanFactory beanFactory;

    @Bean
    RequestDispatcher requestDispatcher(ConversionService conversionService,
                                        List<TelegramMvcConfiguration> telegramMvcConfigurations,
                                        ObjectProvider<TaskExecutor> taskExecutor) {
        TaskExecutor taskExecutorLocal = taskExecutor.getIfAvailable();
        if (taskExecutorLocal == null) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setCorePoolSize(15);
            threadPoolTaskExecutor.setMaxPoolSize(100);
            threadPoolTaskExecutor.initialize();
            taskExecutorLocal = threadPoolTaskExecutor;
        }
        RequestDispatcher requestDispatcher = new RequestDispatcher(handlerMethodContainer(), new HandlerAdapter(conversionService), taskExecutorLocal);
        registerTelegramBotService(requestDispatcher, telegramMvcConfigurations, getOkHttpClientSupplier(taskExecutorLocal));
        return requestDispatcher;
    }

    private Supplier<OkHttpClient> getOkHttpClientSupplier(TaskExecutor taskExecutorLocal) {
        return new Supplier<OkHttpClient>() {
            @Override
            public OkHttpClient get() {
                return new OkHttpClient()
                        .newBuilder()
                        .dispatcher(new Dispatcher(new ExecutorServiceAdapter(taskExecutorLocal)))
                        .build();
            }
        };
    }

    @Bean
    @Scope(value = TelegramScope.SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    TelegramSession telegramSession() {
        return new TelegramSession();
    }

    private void registerTelegramBotService(RequestDispatcher requestDispatcher, List<TelegramMvcConfiguration> telegramMvcConfigurations, Supplier<OkHttpClient> httpClientSupplier) {
        TelegramBotProperties telegramBotProperties = getTelegramBotProperties(telegramMvcConfigurations);
        if (!telegramBotProperties.iterator().hasNext()) {
            logger.warn("Не найдено не одной настройки бота");
        } else {
            for (TelegramBotProperty telegramBotProperty : telegramBotProperties) {
                //Если не задан OkHttpClient создадим свой который будет юзать наш TaskExecutor
                if (telegramBotProperty.getOkHttpClient() == null) {
                    telegramBotProperty =
                            TelegramBotProperty
                                    .newBuilder(telegramBotProperty)
                                    .okHttpClient(memoize(httpClientSupplier).get())
                                    .build();
                }

                TelegramBot telegramBot = createTelegramBot(telegramBotProperty);
                TelegramService telegramService = new TelegramService(telegramBot, requestDispatcher);
                beanFactory.registerSingleton("telegramService" + telegramBotProperty.getAlias(), telegramService);
            }
        }
    }

    @Bean
    @Order()
    ApplicationListener<ContextRefreshedEvent> runnerTelegramService(List<TelegramService> telegramServices) {
        return new ApplicationListener<ContextRefreshedEvent>() {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                for (TelegramService telegramService : telegramServices) {
                    telegramService.start();
                }
            }
        };
    }

    private TelegramBot createTelegramBot(TelegramBotProperty telegramBotProperty) {
        return new TelegramBot.Builder(telegramBotProperty.getToken())
                .okHttpClient(telegramBotProperty.getOkHttpClient())
                .updateListenerSleep(telegramBotProperty.getTimeOutMillis())
                .apiUrl(telegramBotProperty.getUrl())
                .build();
    }

    private TelegramBotProperties getTelegramBotProperties(List<TelegramMvcConfiguration> telegramMvcConfigurations) {
        TelegramBotProperties telegramBotProperties = new TelegramBotProperties();
        ArrayList<TelegramMvcConfiguration> telegramMvcConfigurationsSort = Lists.newArrayList(telegramMvcConfigurations);
        AnnotationAwareOrderComparator.sort(telegramMvcConfigurationsSort);
        for (TelegramMvcConfiguration telegramMvcConfiguration : telegramMvcConfigurationsSort) {
            TelegramBotBuilder telegramBotBuilder = new TelegramBotBuilder();
            telegramMvcConfiguration.configuration(telegramBotBuilder);
            telegramBotProperties.addAll(telegramBotBuilder.build());
        }
        if (telegramBotProperties == null) {
            if (environment.containsProperty(TELEGRAM_BOT_TOKEN)) {
                telegramBotProperties.addTelegramProperty(new TelegramBotProperty(environment.getProperty(TELEGRAM_BOT_TOKEN)));
            }
        }
        return telegramBotProperties;
    }

    @Bean
    HandlerMethodContainer handlerMethodContainer() {
        return new HandlerMethodContainer();
    }

    @Bean
    TelegramControllerBeanPostProcessor telegramControllerBeanPostProcessor() {
        return new TelegramControllerBeanPostProcessor(handlerMethodContainer());
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        beanFactory.registerScope(TelegramScope.SCOPE, new TelegramScope(beanFactory, TimeUnit.HOURS.toMillis(1)));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
