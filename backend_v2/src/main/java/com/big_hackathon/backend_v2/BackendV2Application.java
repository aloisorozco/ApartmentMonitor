package com.big_hackathon.backend_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.big_hackathon.backend_v2.service.EmailService;
import com.big_hackathon.backend_v2.service.UserService;

@SpringBootApplication
public class BackendV2Application {

	public static void main(String[] args) {
		String testEmail = "spring@gmail.com";
		String pass = "123";
		// No need to create context since we have a spring application
		ConfigurableApplicationContext context = SpringApplication.run(BackendV2Application.class, args);

		EmailService es = context.getBean(EmailService.class);

		es.sendSimpleEmail("", "test", "This is a test of the java email sender");
	}

}
