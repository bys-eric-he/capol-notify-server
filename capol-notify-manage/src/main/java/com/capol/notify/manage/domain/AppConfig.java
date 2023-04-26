package com.capol.notify.manage.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AppConfig {
    /**
     * 声明一个线程池，用于执行异步任务
     *
     * @return
     */
    @Bean("Capol-Notify-ThreadPool")
    public Executor myThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Capol-Notify-ThreadPool-");
        executor.initialize();
        log.info("-->声明一个线程池，用于执行异步任务!!!");
        return executor;
    }
}
