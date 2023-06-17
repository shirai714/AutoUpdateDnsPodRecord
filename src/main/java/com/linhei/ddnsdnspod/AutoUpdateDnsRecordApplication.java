package com.linhei.ddnsdnspod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author linhei
 */
@SpringBootApplication
@EnableScheduling
@EnableRedisRepositories
public class AutoUpdateDnsRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoUpdateDnsRecordApplication.class, args);
    }

}
