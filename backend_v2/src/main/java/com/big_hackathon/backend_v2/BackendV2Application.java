package com.big_hackathon.backend_v2;

import com.big_hackathon.backend_v2.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BackendV2Application {

	public static void main(String[] args) {

		String testEmail = "spring@gmail.com";
		String pass = "123";

		double price = 123.45;
		String location = "123 Example Ave";
		String description = "cozy";


		// No need to create context since we have a spring application
		ConfigurableApplicationContext context = SpringApplication.run(BackendV2Application.class, args);

		UserService us = context.getBean(UserService.class);
		us.saveUser(testEmail, pass, "spring", "spring_last");

		ApartmentService as = context.getBean(ApartmentService.class);
		as.insertApartment(price, location, description, "HighResImageLink.test", "ExampleListingURL.com");

		System.out.println(us.getUser("spring@gmail.com"));



	}

}
