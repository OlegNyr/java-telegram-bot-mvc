package com.github.telegram.mvc.handler;

import com.github.telegram.mvc.api.TelegramRequest;
import org.springframework.core.MethodParameter;

/**
 * Имплементация интрефейса должна уметь обрабатывать аргументы метода который обрабатывает запрос
 */
public interface BotHandlerMethodArgumentResolver {
    boolean supportsParameter(MethodParameter parameter);

    /**
     * Праметры метода
     * @param parameter Метаданные параметра
     * @param telegramRequest Описание запроса
     * @return Значение параметра передается
     * @throws Exception Общие ошибки возникают
     */
    Object resolveArgument(MethodParameter parameter, TelegramRequest telegramRequest) throws Exception;
}
