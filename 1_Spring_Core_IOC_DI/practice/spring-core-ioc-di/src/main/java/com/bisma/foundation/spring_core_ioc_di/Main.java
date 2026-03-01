package com.bisma.foundation.spring_core_ioc_di;

import com.bisma.foundation.spring_core_ioc_di.config.AppConfigAnotation;
import com.bisma.foundation.spring_core_ioc_di.config.AppConfigAnotationImport;
import com.bisma.foundation.spring_core_ioc_di.config.AppConfigJava;
import com.bisma.foundation.spring_core_ioc_di.controller.HelloController;
import com.bisma.foundation.spring_core_ioc_di.controller.HelloControllerNonAnotation;
import com.bisma.foundation.spring_core_ioc_di.util.DatabaseConfig;
import org.springframework.context.ApplicationContext;
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

        System.out.println("============ life scyle bean basic ====================");

        AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext(AppConfigJava.class);
        DatabaseConfig dbConfig = context1.getBean(DatabaseConfig.class);
        System.out.println(dbConfig.getConnection());
        context1.close();


        System.out.println("============ Import Scan Based ====================");
        AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext(AppConfigAnotationImport.class);

       HelloControllerNonAnotation helloControllerNonAnotation =  annotationConfigApplicationContext.getBean(HelloControllerNonAnotation.class);

       helloControllerNonAnotation.handleRequest("BoyMan");

        annotationConfigApplicationContext.close();

    }
}
