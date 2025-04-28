package com.big_hackathon.backend_v2;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.big_hackathon.backend_v2.filter.JwtUtil;
import com.big_hackathon.backend_v2.service.UserService;

@SpringBootApplication
public class BackendV2Application {

	public static void main(String[] args) {
		String testEmail = "spring@gmail.com";
		String pass = "123";
		// No need to create context since we have a spring application
		ConfigurableApplicationContext context = SpringApplication.run(BackendV2Application.class, args);

		UserService us = context.getBean(UserService.class);

		try {
			JwtUtil util = new JwtUtil();
			Map<String, String> payload = new HashMap<>();
			payload.put("sub", "1234567890");  
			payload.put("email", "daniel@thebestsweouthere.com"); 
			payload.put("name", "Daniel Daniel"); 
			String jwt = util.generateJWT(payload);

			System.out.println("JWT " + jwt);
			
			System.out.println("Decoded JWT: " + util.decodeNativeToken(jwt).getClaims());
	
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
