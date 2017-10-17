package com.github.telegram.mvc.handler;

import com.github.telegram.mvc.api.TelegramRequest;
import com.github.telegram.mvc.api.TextVariable;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ищет переменные в тексте
 */
public class BotTextVariableMethodArgumentResolver implements BotHandlerMethodArgumentResolver {
    private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache =
            new ConcurrentHashMap<MethodParameter, NamedValueInfo>(256);
    private ConversionService conversionService;

    public BotTextVariableMethodArgumentResolver(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(TextVariable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, TelegramRequest telegramRequest) throws Exception {
        NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Object arg = telegramRequest.getTemplateVariables().get(namedValueInfo.getName());
        if (arg == null) {
            arg = handleNullValue(namedValueInfo.getName(), arg, nestedParameter.getNestedParameterType());
            if (arg == null) {
                if (namedValueInfo.required && !nestedParameter.isOptional()) {
                    throw new RuntimeException("Missing template variable '" + namedValueInfo.getName() +
                            "' for method parameter of type " + parameter.getParameterType().getSimpleName());
                }
            }
        } else {
            if (conversionService.canConvert(arg.getClass(), nestedParameter.getNestedParameterType())) {
                return conversionService.convert(arg, nestedParameter.getNestedParameterType());
            }
        }
        return arg;
    }

    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
        if (namedValueInfo == null) {
            TextVariable annotation = parameter.getParameterAnnotation(TextVariable.class);

            namedValueInfo = new NamedValueInfo(
                    annotation.name() == null ? parameter.getParameterName() : annotation.name(),
                    annotation.required());
            this.namedValueInfoCache.put(parameter, namedValueInfo);
        }
        return namedValueInfo;
    }

    /**
     * A {@code null} results in a {@code false} value for {@code boolean}s or an exception for other primitives.
     */
    private Object handleNullValue(String name, Object value, Class<?> paramType) {
        if (value == null) {
            if (Boolean.TYPE.equals(paramType)) {
                return Boolean.FALSE;
            } else if (paramType.isPrimitive()) {
                throw new IllegalStateException("Optional " + paramType.getSimpleName() + " parameter '" + name +
                        "' is present but cannot be translated into a null value due to being declared as a " +
                        "primitive type. Consider declaring it as object wrapper for the corresponding primitive type.");
            }
        }
        return value;
    }


    private class NamedValueInfo {
        private final String name;

        private final boolean required;

        public NamedValueInfo(String name, boolean required) {
            this.name = name;
            this.required = required;
        }

        public String getName() {
            return name;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
