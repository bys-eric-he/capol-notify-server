package com.capol.notify.producer.port.adapter.restapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要授权的操作
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizedOperation {

    /**
     * 需要鉴权的操作项Key
     */
    String key();

    /**
     * 操作名称
     */
    String name();

    /**
     * 操作描述
     */
    String description() default "";
}
