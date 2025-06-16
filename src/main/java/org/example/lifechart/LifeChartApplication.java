package org.example.lifechart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class LifeChartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeChartApplication.class, args);
    }
}