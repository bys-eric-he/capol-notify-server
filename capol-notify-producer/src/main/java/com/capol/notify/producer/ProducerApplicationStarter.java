package com.capol.notify.producer;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * 生产者服务
 */
@Slf4j
@CrossOrigin
@SpringBootApplication
@ComponentScan(value = "com.capol.notify.*")
@MapperScan(value = "com.capol.notify.manage.domain.repository")
public class ProducerApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplicationStarter.class, args);
        log.info("-->生产者服务启动完成!");
    }
}
