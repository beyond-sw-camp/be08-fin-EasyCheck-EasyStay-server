package com.beyond.easycheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
public class EasycheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasycheckApplication.class, args);
    }

}
