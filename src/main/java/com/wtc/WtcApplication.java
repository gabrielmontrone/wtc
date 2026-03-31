package com.wtc;

import com.wtc.auth.UserDocument;
import com.wtc.auth.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WtcApplication {

    public static void main(String[] args) {
        SpringApplication.run(WtcApplication.class, args);
    }

    // CRIA UM USUÁRIO DE TESTE AUTOMATICAMENTE NO BANCO
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("teste@teste.com").isEmpty()) {
                UserDocument user = new UserDocument();
                user.setEmail("teste@teste.com");
                user.setPassword("123");
                user.setRole("OPERADOR");
                userRepository.save(user);
                System.out.println("USUÁRIO DE TESTE CRIADO: teste@teste.com / 123");
            }
        };
    }
}