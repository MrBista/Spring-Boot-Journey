package com.bisma.foundation.spring_core_ioc_di.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ControllerConfig.class, ServiceConfig.class})
public class AppConfigAnotationImport {
}
