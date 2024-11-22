package com.springapplication.userapp;

import com.springapplication.userapp.config.RsaKeyProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Starting the Spring Boot application
 */

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class UserappApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserappApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx){
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for(String beanName : beanNames){
				System.out.println(beanName);
			}
		};
	}

}
