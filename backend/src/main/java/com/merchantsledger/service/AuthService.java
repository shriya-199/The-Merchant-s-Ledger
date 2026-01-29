package com.merchantsledger.service;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.merchantsledger.config.JwtService;
import com.merchantsledger.dto.AuthRequest;
import com.merchantsledger.dto.AuthResponse;
import com.merchantsledger.dto.RegisterRequest;
import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.entity.Role;
import com.merchantsledger.entity.RoleName;
import com.merchantsledger.entity.User;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.repository.RoleRepository;
import com.merchantsledger.repository.UserRepository;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;
  private final AuditService auditService;

  public AuthService(UserRepository userRepository,
                     RoleRepository roleRepository,
                     PasswordEncoder passwordEncoder,
                     AuthenticationManager authenticationManager,
                     JwtService jwtService,
                     UserService userService,
                     AuditService auditService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userService = userService;
    this.auditService = auditService;
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email is already registered");
    }
    validatePassword(request.getPassword());

    RoleName requestedRole = resolveRole(request.getRoleName());
    if (requestedRole == RoleName.ADMIN) {
      throw new BadRequestException("Admin role cannot be self-assigned");
    }
    Role userRole = roleRepository.findByName(requestedRole)
        .orElseThrow(() -> new BadRequestException("User role not configured"));

    User user = new User();
    user.setFullName(request.getFullName());
    user.setEmail(request.getEmail());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    user.setPhone(request.getPhone());
    user.setCompanyName(request.getCompanyName());
    user.setAddress(request.getAddress());
    user.setRoleTitle(request.getRoleTitle());
    user.setRoles(Collections.singleton(userRole));

    User saved = userRepository.save(user);
    String token = jwtService.generateToken(saved);
    UserResponse response = userService.toResponse(saved);
    auditService.log(saved, "USER_REGISTER", "User", String.valueOf(saved.getId()), requestedRole.name());
    return new AuthResponse(token, response);
  }

  public AuthResponse login(AuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    if (!user.isEnabledFlag()) {
      throw new BadRequestException("Account is disabled");
    }

    String token = jwtService.generateToken(user);
    UserResponse response = userService.toResponse(user);
    auditService.log(user, "USER_LOGIN", "User", String.valueOf(user.getId()), "Login successful");
    return new AuthResponse(token, response);
  }

  private void validatePassword(String password) {
    if (password == null || password.length() < 8) {
      throw new BadRequestException("Password must be at least 8 characters");
    }
    boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
    boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
    boolean hasDigit = password.chars().anyMatch(Character::isDigit);
    boolean hasSpecial = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;':\",.<>?/`~".indexOf(ch) >= 0);
    if (!(hasUpper && hasLower && hasDigit && hasSpecial)) {
      throw new BadRequestException("Password must include upper, lower, number, and symbol");
    }
  }

  private RoleName resolveRole(String roleName) {
    if (roleName == null || roleName.isBlank()) {
      return RoleName.STAFF;
    }
    try {
      return RoleName.valueOf(roleName.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("Invalid role selection");
    }
  }
}
