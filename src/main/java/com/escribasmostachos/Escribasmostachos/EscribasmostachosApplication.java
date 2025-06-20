package com.escribasmostachos.Escribasmostachos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class EscribasmostachosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EscribasmostachosApplication.class, args);
	}

}
