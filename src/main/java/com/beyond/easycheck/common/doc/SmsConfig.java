package com.beyond.easycheck.common.doc;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {
    @Bean
    public DefaultMessageService defaultMessageService(
            @Value("${coolsms.easycheck.apikey}") String apiKey,
            @Value("${coolsms.easycheck.apisecret}") String apiSecret) {
        return NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }
}