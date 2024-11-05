package com.documentparserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DocumentParserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentParserServiceApplication.class, args);
	}

}
