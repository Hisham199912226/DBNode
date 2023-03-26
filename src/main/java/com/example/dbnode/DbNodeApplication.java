package com.example.dbnode;


import com.example.dbnode.authentication.nodes.AddJwtTokenFilter;
import com.example.dbnode.authentication.nodes.NodesJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

//exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}
@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@RequiredArgsConstructor
public class DbNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbNodeApplication.class, args);
	}
	@Bean
	public WebClient webClient(){
		NodesJwtService nodesJwtService = new NodesJwtService();
		AddJwtTokenFilter addJwtTokenFilter = new AddJwtTokenFilter(nodesJwtService);
		return WebClient.builder().filter(addJwtTokenFilter).build();
	}
}
