package gr.leonzch.sudoku.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**", "/api/error", "/api/actuator/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/api/sudoku", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/api/user/login")
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
