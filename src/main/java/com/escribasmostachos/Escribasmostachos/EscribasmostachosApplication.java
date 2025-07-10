package com.escribasmostachos.Escribasmostachos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.escribasmostachos.Escribasmostachos.repository")
public class EscribasmostachosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EscribasmostachosApplication.class, args);
	}

}
