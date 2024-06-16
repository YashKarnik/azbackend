package com.azbackend.azbackend;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AzbackendApplication {

	public static void main(String[] args) {
		ApplicationInsights.attach();
		SpringApplication.run(AzbackendApplication.class, args);
	}

}
