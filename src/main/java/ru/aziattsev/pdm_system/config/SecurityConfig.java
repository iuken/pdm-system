package ru.aziattsev.pdm_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(PathRequest.toH2Console()).permitAll().requestMatchers("/", "/h2-console/**", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll().requestMatchers("/api/public/**").permitAll().requestMatchers("/api/**").authenticated().anyRequest().authenticated()).formLogin(form -> form.loginPage("/auth/login").defaultSuccessUrl("/home").failureUrl("/auth/login?error=true").permitAll()).logout(logout -> logout.logoutUrl("/auth/logout")  // URL для выхода
                .logoutSuccessUrl("/auth/login?logout=true")  // Перенаправление после выхода
                .invalidateHttpSession(true)  // Очистка сессии
                .deleteCookies("JSESSIONID")  // Удаление cookies
                .permitAll()).csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()).ignoringRequestMatchers("/api/**")).httpBasic(withDefaults()).headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin).contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'")));

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}