package com.bisma.foundation.aop_learn;

import com.bisma.foundation.aop_learn.config.AppConfig;
import com.bisma.foundation.aop_learn.controller.NotificationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AopLearnApplication {

	public static void main(String[] args) {

//		SpringApplication.run(AopLearnApplication.class, args);

		ApplicationContext context = SpringApplication.run(AppConfig.class);
		NotificationController notificationController =
				context.getBean("notificationController", NotificationController.class);


		notificationController.send();
		try{
			notificationController.sendTrhowTest();
		}catch (RuntimeException e) {
			System.out.println("...");
		} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
