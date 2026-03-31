package com.wtc.config;

import com.wtc.auth.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // <--- Importante: Ativa a segurança de verdade
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    // Injetamos o filtro que você criou no passo anterior
    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Desativa CSRF (padrão para APIs REST)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API sem estado (usando JWT)
                .authorizeHttpRequests(authorize -> authorize
                        // Libera o acesso ao login (Senão ninguém consegue entrar!)
                        // .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll() -> momentaneo

                        // Bloqueia todo o resto (Exige token para ver histórico, mensagens, etc)
                        // .anyRequest().authenticated() -> momentaneo

                        .anyRequest().permitAll()
                )
                // Coloca o SEU filtro antes do filtro padrão do Spring
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}