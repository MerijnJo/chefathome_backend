package com.example.chefbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChefbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChefbackendApplication.class, args);
    }

}
