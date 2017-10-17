package com.github.telegram.mvc.handler;

import com.github.telegram.mvc.api.TelegramRequest;
import org.springframework.core.MethodParameter;

/**
 * Имплементация интерфейса умеет преобразовывать возвращаемый параметр, заполняет в {@link TelegramRequest} параметр baseRequest
 */
public interface BotHandlerMethodReturnValueHandler {

    boolean supportsReturnType(MethodParameter returnType);

    void handleReturnValue(Object returnValue, MethodParameter returnType, TelegramRequest telegramRequest) throws Exception;
}
