package com.example.demo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.demo.auth.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 👉 Esto le dice a Spring:
// "creame un objeto JwtProperties con lo del application.properties"
@EnableConfigurationProperties(JwtProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
