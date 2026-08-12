package com.example.chat_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatServerApplication {

    public static void main(String[] args) {

        System.out.println("1. main 시작");

        SpringApplication.run(ChatServerApplication.class, args);

        System.out.println("2. main 끝");
    }

}
