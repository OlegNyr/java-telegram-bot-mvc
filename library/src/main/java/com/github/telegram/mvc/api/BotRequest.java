package com.github.telegram.mvc.api;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static com.github.telegram.mvc.api.MessageType.COMMAND;
import static com.github.telegram.mvc.api.MessageType.MESSAGE;


@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BotRequest {

    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};

    MessageType[] messageType() default {MESSAGE, COMMAND};
}
