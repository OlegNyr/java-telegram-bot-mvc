package com.github.telegram.mvc.api;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextVariable {

    /**
     * Алиас для параметра
     * @return Имя переменных из запроса
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Имя параметра
     * @return Имя переменных из запроса
     *
     */
    @AliasFor("value")
    String name() default "";

    boolean required() default true;

}
