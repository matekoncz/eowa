package com.example.eowa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class EowaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EowaApplication.class, args);
	}

}
