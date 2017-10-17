package com.github.telegram.mvc.config;


public class TelegramScopeException extends RuntimeException {
    public TelegramScopeException(String message) {
        super(message);
    }

    public TelegramScopeException(Throwable e) {
        super(e);
    }
}
