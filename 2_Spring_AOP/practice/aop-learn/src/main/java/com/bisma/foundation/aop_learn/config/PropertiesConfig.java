package com.bisma.foundation.aop_learn.config;

import com.bisma.foundation.aop_learn.properties.EmailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "email")
    public EmailProperties emailProperties() {
        return new EmailProperties();
    }
}
