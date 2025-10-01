package com.example.cypersoft.DoAnJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.cypersoft.DoAnJava")
public class DoAnJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoAnJavaApplication.class, args);
	}

}
