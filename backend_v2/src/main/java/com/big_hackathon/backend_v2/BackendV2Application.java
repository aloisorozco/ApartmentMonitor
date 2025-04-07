package com.big_hackathon.backend_v2;

import com.big_hackathon.backend_v2.controller.ApartmentController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.big_hackathon.backend_v2.repo.ApartmentDAO;

@SpringBootApplication
public class BackendV2Application {

	public static void main(String[] args) {

		// No need to create context since we have a spring application
		ConfigurableApplicationContext context = SpringApplication.run(BackendV2Application.class, args);

		ApartmentController aController = context.getBean(ApartmentController.class);
		aController.listApartments();

	}

}
