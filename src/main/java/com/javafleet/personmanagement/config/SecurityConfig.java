package com.javafleet.personmanagement.config;

import com.javafleet.personmanagement.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // NEU! Method Security aktivieren
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Öffentliche URLs
                .requestMatchers("/", "/login", "/register","/h2-console/**").permitAll()
                
                // Admin-Only URLs
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // API für User und Admin
                .requestMatchers("/api/persons/**").hasAnyRole("USER", "ADMIN")
                
                // Alle anderen brauchen Login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/persons", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("mySecretRememberMeKey")
                .tokenValiditySeconds(86400 * 30)
                .rememberMeParameter("remember-me")
                .userDetailsService(userDetailsService)
            )
            .httpBasic(basic -> {})
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
