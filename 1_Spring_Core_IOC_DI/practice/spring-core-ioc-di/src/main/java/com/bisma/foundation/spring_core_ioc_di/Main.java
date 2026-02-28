package com.bisma.foundation.spring_core_ioc_di;

import com.bisma.foundation.spring_core_ioc_di.config.AppConfigAnotation;
import com.bisma.foundation.spring_core_ioc_di.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        System.out.println("============ versi Anotation Configuration ============");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfigAnotation.class);


        HelloController helloController = context.getBean(HelloController.class);

        helloController.handleRequest("Joko");
        helloController.handleRequest("widodo");
        context.close();


        System.out.println("============ versi Configration Java Based ====================");
        AnnotationConfigApplicationContext contextJavaBased = new AnnotationConfigApplicationContext(AppConfigAnotation.class);
        HelloController contextJavaBasedBean = contextJavaBased.getBean(HelloController.class);
        contextJavaBasedBean.handleRequest("wowo");
        contextJavaBased.close();
    }
}
