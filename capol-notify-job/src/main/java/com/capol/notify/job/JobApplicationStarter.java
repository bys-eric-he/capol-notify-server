package com.capol.notify.job;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(value = "com.capol.notify.*")
@MapperScan(value = "com.capol.notify.manage.domain.repository")
public class JobApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(JobApplicationStarter.class, args);
        log.info("-->消息监控Job服务启动完成!");
    }
}
