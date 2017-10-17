package com.github.telegram.mvc.handler;

import com.github.telegram.mvc.api.TelegramRequest;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.HttpMessageConverter;

import java.lang.reflect.Type;

public class BotResponseBodyMethodProcessor implements BotHandlerMethodReturnValueHandler {
    private ConversionService conversionService;

    public BotResponseBodyMethodProcessor(ConversionService conversionService) {

        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, TelegramRequest telegramRequest) throws Exception {
        Object outputValue;
        Class<?> valueType;

        if (returnValue instanceof CharSequence) {
            outputValue = returnValue.toString();
        } else {
            outputValue = returnValue;
            valueType = getReturnValueType(outputValue, returnType);
            if (conversionService.canConvert(valueType, String.class)) {
                outputValue = conversionService.convert(outputValue, String.class);
            } else if (conversionService.canConvert(returnType.getParameterType(), String.class)) {
                outputValue = conversionService.convert(returnType.getParameterType(), String.class);
            }
        }
        telegramRequest.setBaseRequest(new SendMessage(telegramRequest.chatId(), (String) outputValue));
    }

    private Class<?> getReturnValueType(Object value, MethodParameter returnType) {
        return (value != null ? value.getClass() : returnType.getParameterType());
    }

}
