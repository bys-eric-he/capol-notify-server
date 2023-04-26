package com.capol.notify.manage.domain.model.message;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 消息编辑的切面
 */
@Slf4j
@Component
@Aspect
public class MQMessageEditAspect {

    /**
     * 定义切面的切点
     */
    @Pointcut("@annotation(com.capol.notify.manage.domain.model.message.MQMessageEdit)")
    private void pointCut() {

    }

    /**
     * 返回通知（After Returning Advice）：在目标方法执行后调用，只有在目标方法正常返回时才会调用。
     *
     * @param joinPoint
     * @param mqMessageEdit
     * @param res
     */
    @AfterReturning(value = "pointCut() && @annotation(mqMessageEdit)", returning = "res")
    public void afterReturning(JoinPoint joinPoint, MQMessageEdit mqMessageEdit, Object res) {
        Object object = joinPoint.getArgs()[mqMessageEdit.argsIndex()];

        log.info("-->消息消费切面执行成功:{}", object);
    }

    /**
     * 异常通知（After Throwing Advice）：在目标方法抛出异常时调用。
     *
     * @param joinPoint
     * @param mqMessageEdit
     * @param exception
     */
    @AfterThrowing(value = "pointCut() && @annotation(mqMessageEdit)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, MQMessageEdit mqMessageEdit, Throwable exception) {
        Object object = joinPoint.getArgs()[mqMessageEdit.argsIndex()];

        log.info("-->消息消费切面执行异常:{}", object);
    }
}
