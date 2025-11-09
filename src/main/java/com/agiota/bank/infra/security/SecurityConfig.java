package com.agiota.bank.infra.security;

import com.agiota.bank.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/user").permitAll()

                        // Configuração do Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-resources"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/v1/loans").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/v1/loans/{id}/pay").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/v1/loans/{id}/approval").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/support-tickets").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/v1/notifications/**").hasRole("USER") 
                        .requestMatchers(HttpMethod.DELETE, "/v1/notifications/**").hasRole("USER") 
                        .requestMatchers(HttpMethod.POST, "/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/**").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint(authenticationEntryPoint())  // Erros de autenticação (ex: 401)
                    .accessDeniedHandler(accessDeniedHandler())  // Erros de autorização (ex: 403)
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build(); 
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Acesso Negado", "Você não tem permissão para acessar este recurso.");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        };
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Não Autorizado", "Token de autenticação inválido ou expirado.");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}