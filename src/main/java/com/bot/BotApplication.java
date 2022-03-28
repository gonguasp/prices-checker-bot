package com.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BotApplication {

	public static ApplicationContext applicationContext = null;

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(BotApplication.class, args);
	}
}
