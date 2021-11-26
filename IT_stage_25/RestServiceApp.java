package com.it;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class RestServiceApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RestServiceApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
