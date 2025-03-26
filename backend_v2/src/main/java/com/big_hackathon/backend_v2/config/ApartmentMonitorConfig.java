package com.big_hackathon.backend_v2.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"repo.ApartmentDAO", "repo.UserDAO"})
public class ApartmentMonitorConfig {
    
}
