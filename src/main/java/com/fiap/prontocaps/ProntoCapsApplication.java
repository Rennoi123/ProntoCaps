package com.fiap.prontocaps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProntoCapsApplication {
  public static void main(String[] args) {
    SpringApplication.run(ProntoCapsApplication.class, args);
  }
}
