package com.capol.notify.job.application;

import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoConstructorJobHandler extends IJobHandler {
    @Override
    public void execute() throws Exception {
        log.info("-->这是一个没有定义带参数构造器的JobHandler!!!!");
    }
}
