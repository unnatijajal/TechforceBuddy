package com.mlmodeltrainingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class MlModelTrainigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MlModelTrainigServiceApplication.class, args);
	}

}
