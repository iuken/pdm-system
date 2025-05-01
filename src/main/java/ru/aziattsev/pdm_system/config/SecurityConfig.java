package ru.aziattsev.pdm_system.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // Разрешаем точку с запятой
        return firewall;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к H2 Console и статическим ресурсам
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/", "/h2-console/**", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/home")
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")  // URL для выхода
                        .logoutSuccessUrl("/auth/login?logout=true")  // Перенаправление после выхода
                        .invalidateHttpSession(true)  // Очистка сессии
                        .deleteCookies("JSESSIONID")  // Удаление cookies
                        .permitAll()
                )
                // Настройки для H2 Console
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console())
                )
                .httpBasic(Customizer.withDefaults()) // Включаем Basic Auth
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF для API
                .headers(headers -> headers
                        .frameOptions().disable()  // Разрешаем фреймы для H2 Console
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}