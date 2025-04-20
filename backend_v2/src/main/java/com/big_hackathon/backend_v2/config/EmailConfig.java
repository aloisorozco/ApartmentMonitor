package com.big_hackathon.backend_v2.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Autowired
    private org.springframework.core.env.Environment environment; // To access properties

    public static class EmailCredentials {
        private String email_address;
        private String email_password;
    
        
        public String getEmail_address() {
            return email_address;
        }
    
        public void setEmail_address(String email_address) {
            this.email_address = email_address;
        }
    
        public String getEmail_password() {
            return email_password;
        }
    
        public void setEmail_password(String email_password) {
            this.email_password = email_password;
        }
    }

    @Bean
    public EmailCredentials emailCredentials() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
            new ClassPathResource("email_cred.json").getInputStream(),
            EmailCredentials.class
        );
    }

    @Bean
    public JavaMailSender getJavaMailSender(EmailCredentials creds) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(environment.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(environment.getProperty("spring.mail.port")));

        // Set credentials loaded at runtime
        mailSender.setUsername(creds.getEmail_address());
        mailSender.setPassword(creds.getEmail_password());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        

        return mailSender;
    }

    
}