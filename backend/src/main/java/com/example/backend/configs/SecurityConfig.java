package com.example.backend.configs;
 
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 
 import static org.springframework.security.config.Customizer.withDefaults;
 
 import com.example.backend.helpers.JwtAuthFilter;
 
 import lombok.RequiredArgsConstructor;
 
 @RequiredArgsConstructor
 @Configuration
 @EnableWebSecurity
 public class SecurityConfig {
     private final JwtAuthFilter jwtAuthFilter;
 
     @Bean
     public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
     }
 
     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.csrf(csrf -> csrf.disable())
             .cors(withDefaults())
             .authorizeHttpRequests(auth -> auth
                 
                 .requestMatchers(
                     "/api/v1/auth/login",
                     "/api/v1/auth/refresh_token"
                 )
                 .permitAll()
 
                 .anyRequest().authenticated()
             
             )
             .sessionManagement(session -> session 
                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
             )
             .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
 
         return http.build();
     }
 }