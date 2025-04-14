package com.big_hackathon.backend_v2;

import com.big_hackathon.backend_v2.service.JavaWebScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.big_hackathon.backend_v2.service.UserService;

import java.util.Map;

@SpringBootApplication
public class BackendV2Application {

	public static void main(String[] args) {
		String testEmail = "spring@gmail.com";
		String pass = "123";
		// No need to create context since we have a spring application
		ConfigurableApplicationContext context = SpringApplication.run(BackendV2Application.class, args);

//		UserService us = context.getBean(UserService.class);
//
//		System.out.println(us.saveUser(testEmail, pass, "spring", "spring_last"));

		JavaWebScraper test = context.getBean(JavaWebScraper.class);

		System.out.println(test.scrapeKijiji("https://www.kijiji.ca/v-apartments-condos/winnipeg/owen-apartments-studio-apartment-for-rent/1714854695"));
	}

}
