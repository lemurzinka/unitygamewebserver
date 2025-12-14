package dev.unity.backend.gamebackend.config;

import dev.unity.backend.gamebackend.services.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors() 
        .and()  
        .csrf().disable()
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**", "/stripe/webhook", "/api/logs/**", "/stripe/test").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/users/*/balance").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/skins").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/skins/*/image").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/skins/upload").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/nlp/analyze").permitAll()
    .requestMatchers("/api/skins/*/buy").authenticated()
    .requestMatchers("/api/skins/*/select").authenticated()
    .requestMatchers("/stripe/create-checkout-session").authenticated()
    .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .anyRequest().authenticated()
)


        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
