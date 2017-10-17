package com.github.telegram.mvc.api;

import com.github.telegram.mvc.TelegramConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TelegramConfiguration.class)
public @interface EnableTelegram {
}
