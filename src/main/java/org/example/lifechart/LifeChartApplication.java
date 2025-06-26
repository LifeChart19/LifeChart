package org.example.lifechart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableCaching
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableFeignClients
public class LifeChartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeChartApplication.class, args);
    }
}