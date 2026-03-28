package com.techcup.techcup_futbol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.techcup.techcup_futbol.repository")
public class TechcupFutbolApplication {

    public static void main(String[] args) {

        SpringApplication.run(TechcupFutbolApplication.class, args);
    }
}