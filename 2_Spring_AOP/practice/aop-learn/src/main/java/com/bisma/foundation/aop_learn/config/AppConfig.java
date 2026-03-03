package com.bisma.foundation.aop_learn.config;

import com.bisma.foundation.aop_learn.aop.LoggingAspect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Configuration
@Import({ServiceConfig.class, PropertiesConfig.class, ControllerConfig.class, RepositoryConfig.class, LoggingAspect.class})
@EnableAspectJAutoProxy
@EnableConfigurationProperties
public class AppConfig {
}
