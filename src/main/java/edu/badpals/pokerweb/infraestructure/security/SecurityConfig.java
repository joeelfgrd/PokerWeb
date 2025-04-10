package edu.badpals.pokerweb.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.modelmapper.ModelMapper;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/usuarios/login",
                                "/api/usuarios/registro",
                                "/h2-console/**",
                                "/api/partidas/crear",
                                "/api/partidas/{id}/mano-privada",
                                "/api/partidas/{id}/nueva-mano",
                                "/api/partidas/unirse",
                                "/api/partidas/{id}/showdown",
                                "/api/partidas/{id}/apostar",
                                "/api/partidas/{id}/igualar",
                                "/api/partidas/{id}/pasar",
                                "/api/partidas/{id}/retirarse",
                                "/api/partidas/{id}/allin",
                                "/api/partidas/{id}/estado"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .httpBasic(Customizer.withDefaults())
                .build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}