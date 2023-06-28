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
     * 获取当前活跃CPU数量
     */
    private static int NUMBER_OF_CORES = Math.max(4, Math.min(Runtime.getRuntime().availableProcessors(), 16));

    /**
     * 声明一个线程池，用于执行异步任务
     *
     * @return
     */
    @Bean("Capol-Notify-ThreadPool")
    public Executor myThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        log.info("-->声明线程池, 当前活跃的CPU数量:{}, CorePoolSize:{}, MaxPoolSize:{}", NUMBER_OF_CORES, NUMBER_OF_CORES + 2, NUMBER_OF_CORES * 2);
        //核心线程池大小, 线程池中最少会保留的线程数量, 配置参数必须小于MaxPoolSize
        executor.setCorePoolSize(NUMBER_OF_CORES + 2);
        //线程池中最大的线程数, 当任务量达到一定程度时, 线程池中的线程数量会动态增加, 直到达到MaxPoolSize后, 这些任务会被放置在任务队列中等待执行
        executor.setMaxPoolSize(NUMBER_OF_CORES * 2);
        //任务队列容量, 当任务队列已满时, 新的任务将会被拒绝。
        executor.setQueueCapacity(25);
        //空闲线程存活时间
        executor.setKeepAliveSeconds(5);
        //线程名称前缘
        executor.setThreadNamePrefix("Capol-Notify-ThreadPool-");
        executor.initialize();
        log.info("-->声明一个线程池，用于执行异步任务!!!");
        return executor;
    }
}
