package com.example.dbnode;

import com.example.dbnode.api.bootstrap.model.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@RequiredArgsConstructor
public class DbNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbNodeApplication.class, args);
	}
	@Bean
	public WebClient webClient(){
		return WebClient.builder().build();
	}
}
