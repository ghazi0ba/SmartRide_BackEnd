package com.smartride.smartride_payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class SmartridePaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartridePaymentServiceApplication.class, args);
	}

}
