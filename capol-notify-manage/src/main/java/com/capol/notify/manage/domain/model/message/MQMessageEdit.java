package com.capol.notify.manage.domain.model.message;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQMessageEdit {
    /**
     * 需要记录的消息对象参数位置
     *
     * @return
     */
    int argsIndex();
}
