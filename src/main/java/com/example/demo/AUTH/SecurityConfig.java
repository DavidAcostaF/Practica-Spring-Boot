package com.example.demo.AUTH;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private JWTUtilityService jwtUtilityService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authRequest-> authRequest.requestMatchers("/auth/**", "/login", "/register", "/public/**", "/css/**", "/js/**", "/images/**").permitAll().
                                anyRequest().
                                authenticated())
                .sessionManagement(
                        sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JWTAuthorizationFilter(jwtUtilityService),UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exceptionHandling->
                                exceptionHandling
                                        .authenticationEntryPoint((request, response, authException) -> {
                                        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                                        response.sendRedirect("/login");
                                    }))
                .logout(logout -> {
                    logout.logoutSuccessUrl("/logout");
                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"));
                    logout.logoutSuccessUrl("/login");
                    logout.deleteCookies("token"); } )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}