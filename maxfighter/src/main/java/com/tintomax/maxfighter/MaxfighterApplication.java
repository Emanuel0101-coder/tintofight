package com.tintomax.maxfighter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MaxfighterApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MaxfighterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" +
                "==============================================\n" +
                "🔥 MAXFIGHTER INICIADO COM SUCESSO! 🔥\n" +
                "➡ Batalha funcionando e rodando em:\n" +
                "   http://localhost:8080/battle/select\n" +
                "==============================================\n");
    }
}
