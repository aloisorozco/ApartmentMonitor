package com.big_hackathon.backend_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.big_hackathon.backend_v2.repo.UserDAO;

@SpringBootApplication
public class BackendV2Application {

	public static void main(String[] args) {
		
		String testEmail = "spring@gmail.com";
		String pass = "123";
		// No need to create context since we have a spring application
		ConfigurableApplicationContext context = SpringApplication.run(BackendV2Application.class, args);

		UserDAO uDAO = context.getBean(UserDAO.class);

		System.out.println(uDAO.saveUser(testEmail, pass, "spring", "spring_last"));
		System.out.println(uDAO.getUser(testEmail));
		System.out.println(uDAO.delUser(testEmail));
	}

}
