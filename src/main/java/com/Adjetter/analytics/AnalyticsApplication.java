package com.Adjetter.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
public class AnalyticsApplication {
	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));   // It will set UTC timezone
		System.out.println("\nSpring boot application running in UTC timezone :"+new Date() +"\n");
	}

	public static void main(String[] args) {
		SpringApplication.run(AnalyticsApplication.class, args);
	}

}
