package com.bisma.foundation.fundamental_spring_boot;

import com.bisma.foundation.fundamental_spring_boot.config.AppConfiguration;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class FundamentalSpringBootApplicationTests {

	@Autowired
	private ApplicationContext context;


	@Test
	void testConfigBean() {
		AppConfiguration config = context.getBean("appConfig", AppConfiguration.class);

		System.out.println(config.getName());
		assertNotNull(config);

		assertEquals("MrBista-Foundation-App", config.getName());
		assertTrue( config.getVersion() > 0);
		assertEquals("Hai Dunia", config.getGreetingHello());
		assertEquals(20, config.getFeature().getMaxUsers());


	}


}
