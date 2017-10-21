package com.github.telegram.mvc;

import com.github.telegram.mvc.api.TelegramRequest;
import com.github.telegram.mvc.handler.*;
import com.pengrad.telegrambot.request.BaseRequest;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Вызывает обработчик запроса, подготавливает параметры метода для выполнения
 */
public class HandlerAdapter {

    private final ConversionService conversionService;
    private final BotHandlerMethodArgumentResolverComposite argumentResolvers;
    private final BotHandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public HandlerAdapter(ConversionService conversionService) {
        this.conversionService = conversionService;

        List<BotHandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
        this.argumentResolvers = new BotHandlerMethodArgumentResolverComposite().addResolvers(resolvers);

        List<BotHandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
        this.returnValueHandlers = new BotHandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
    }

    /**
     * Вызывает медот представленный в handlerMethod
     *
     * @param telegramRequest описание сообщение
     * @param handlerMethod   описание медода который нужно вызвать
     * @return Возвращает ответ который нужно передать пользователь
     * @throws Exception пробрасывает все ошибки
     */
    BaseRequest handle(TelegramRequest telegramRequest, HandlerMethod handlerMethod) throws Exception {
        TelegramInvocableHandlerMethod invocableMethod = new TelegramInvocableHandlerMethod(handlerMethod);
        invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        invocableMethod.invokeAndHandle(telegramRequest);
        return telegramRequest.getBaseRequest();
    }


    private List<BotHandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<BotHandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        // Annotation-based argument resolution
        resolvers.add(new BotTextVariableMethodArgumentResolver(conversionService));
        resolvers.add(new BotRequestMethodArgumentResolver());
        return resolvers;
    }

    public List<BotHandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<BotHandlerMethodReturnValueHandler> valueHandlers = new ArrayList<>();
        valueHandlers.add(new BotBaseRequestMethodProcessor());
        valueHandlers.add(new BotResponseBodyMethodProcessor(conversionService));
        return valueHandlers;
    }
}
