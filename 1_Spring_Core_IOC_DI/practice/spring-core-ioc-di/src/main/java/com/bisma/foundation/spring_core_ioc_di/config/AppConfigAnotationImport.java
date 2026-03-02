package com.bisma.foundation.spring_core_ioc_di.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({ControllerConfig.class, ServiceConfig.class, PropertiesConfig.class})
@EnableConfigurationProperties
public class AppConfigAnotationImport {
}
