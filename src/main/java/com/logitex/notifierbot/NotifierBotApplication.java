package com.logitex.notifierbot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@RequiredArgsConstructor
@SpringBootApplication
@EnableScheduling
public class NotifierBotApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NotifierBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
