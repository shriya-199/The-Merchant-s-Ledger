package com.merchantsledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import com.merchantsledger.service.UserService;

@Configuration
public class SecurityConfig {
  private final JwtAuthFilter jwtAuthFilter;
  private final UserService userService;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserService userService) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.userService = userService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/ws/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/audit/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers("/api/analytics/**", "/api/exports/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers("/api/notifications/**").hasAnyRole("ADMIN", "MANAGER", "STAFF", "USER")
            .requestMatchers(HttpMethod.GET, "/api/warehouses/**", "/api/products/**",
                "/api/inventory/**", "/api/customers/**", "/api/ledger/**")
                .hasAnyRole("ADMIN", "MANAGER", "STAFF", "USER")
            .requestMatchers(HttpMethod.POST, "/api/warehouses/**", "/api/products/**",
                "/api/customers/**", "/api/ledger/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.POST, "/api/warehouses/**", "/api/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/warehouses/**", "/api/products/**",
                "/api/customers/**", "/api/users/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**", "/api/products/**",
                "/api/customers/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
