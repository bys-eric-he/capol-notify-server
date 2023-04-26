package com.capol.notify.consumer;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 消费者服务
 */
@Slf4j
@SpringBootApplication
@ComponentScan(value = "com.capol.notify.*")
@MapperScan(value = "com.capol.notify.manage.domain.repository")
public class ConsumerApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplicationStarter.class, args);
        log.info("-->消费者服务启动完成!");

    }
}
