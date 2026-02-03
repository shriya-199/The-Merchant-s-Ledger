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
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/google",
                "/api/auth/otp/send", "/api/auth/login/send-otp", "/ws/**").permitAll()
            .requestMatchers("/api/admin/**").hasAnyRole("SYSTEM_ADMIN", "MERCHANT_ADMIN", "ADMIN")
            .requestMatchers("/api/audit/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "WAREHOUSE_MANAGER", "INVENTORY_AUDITOR", "ADMIN", "MANAGER")
            .requestMatchers("/api/analytics/**", "/api/exports/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "MERCHANT_OPERATIONS", "WAREHOUSE_MANAGER", "ADMIN", "MANAGER")
            .requestMatchers("/api/notifications/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "MERCHANT_FINANCE", "MERCHANT_OPERATIONS", "MERCHANT_VIEWER",
                "WAREHOUSE_MANAGER", "INVENTORY_AUDITOR", "PICKER_PACKER", "RECEIVER_GRN_OPERATOR",
                "ADMIN", "MANAGER", "STAFF", "USER")
            .requestMatchers(HttpMethod.GET, "/api/warehouses/**", "/api/products/**", "/api/inventory/**",
                "/api/customers/**", "/api/ledger/**", "/api/reconciliation/**")
                .hasAnyRole(
                    "SYSTEM_ADMIN", "SUPPORT_AGENT", "MERCHANT_ADMIN", "MERCHANT_FINANCE", "MERCHANT_OPERATIONS",
                    "MERCHANT_VIEWER", "WAREHOUSE_MANAGER", "INVENTORY_AUDITOR", "PICKER_PACKER",
                    "RECEIVER_GRN_OPERATOR", "ADMIN", "MANAGER", "STAFF", "USER")
            .requestMatchers(HttpMethod.POST, "/api/ledger/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "MERCHANT_FINANCE", "ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "MERCHANT_OPERATIONS", "WAREHOUSE_MANAGER", "INVENTORY_AUDITOR",
                "PICKER_PACKER", "RECEIVER_GRN_OPERATOR", "ADMIN", "MANAGER", "STAFF")
            .requestMatchers(HttpMethod.POST, "/api/reconciliation/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "WAREHOUSE_MANAGER", "INVENTORY_AUDITOR", "ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.POST, "/api/warehouses/**", "/api/products/**", "/api/customers/**").hasAnyRole(
                "SYSTEM_ADMIN", "MERCHANT_ADMIN", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/users/me", "/api/users/me/**").hasAnyRole(
                "SYSTEM_ADMIN", "SUPPORT_AGENT", "MERCHANT_ADMIN", "MERCHANT_FINANCE", "MERCHANT_OPERATIONS",
                "MERCHANT_VIEWER", "WAREHOUSE_MANAGER", "INVENTORY_AUDITOR", "PICKER_PACKER",
                "RECEIVER_GRN_OPERATOR", "ADMIN", "MANAGER", "STAFF", "USER")
            .requestMatchers(HttpMethod.PUT, "/api/warehouses/**", "/api/products/**",
                "/api/customers/**", "/api/users/**").hasAnyRole(
                    "SYSTEM_ADMIN", "MERCHANT_ADMIN", "MERCHANT_OPERATIONS", "WAREHOUSE_MANAGER", "ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**", "/api/products/**", "/api/customers/**")
                .hasAnyRole("SYSTEM_ADMIN", "MERCHANT_ADMIN", "ADMIN")
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
