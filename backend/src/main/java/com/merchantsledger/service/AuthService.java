package com.merchantsledger.service;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.merchantsledger.config.JwtService;
import com.merchantsledger.dto.AuthRequest;
import com.merchantsledger.dto.AuthResponse;
import com.merchantsledger.dto.GoogleTokenInfo;
import com.merchantsledger.dto.OtpChallengeResponse;
import com.merchantsledger.dto.OtpPurpose;
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
  private static final Set<RoleName> SELF_ASSIGNABLE_ROLES = Set.of(
      RoleName.MERCHANT_OPERATIONS,
      RoleName.MERCHANT_VIEWER,
      RoleName.MERCHANT_FINANCE,
      RoleName.PICKER_PACKER,
      RoleName.RECEIVER_GRN_OPERATOR,
      RoleName.STAFF,
      RoleName.MANAGER,
      RoleName.USER
  );

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;
  private final AuditService auditService;
  private final NotificationService notificationService;
  private final OtpService otpService;
  private final RestTemplate restTemplate;
  private final String googleClientId;

  public AuthService(UserRepository userRepository,
                     RoleRepository roleRepository,
                     PasswordEncoder passwordEncoder,
                     AuthenticationManager authenticationManager,
                     JwtService jwtService,
                     UserService userService,
                     AuditService auditService,
                     NotificationService notificationService,
                     OtpService otpService,
                     RestTemplateBuilder restTemplateBuilder,
                     @Value("${app.google.clientId:}") String googleClientId) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userService = userService;
    this.auditService = auditService;
    this.notificationService = notificationService;
    this.otpService = otpService;
    this.restTemplate = restTemplateBuilder.build();
    this.googleClientId = googleClientId;
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email is already registered");
    }
    validatePassword(request.getPassword());
    if (request.getPhone() == null || request.getPhone().isBlank()) {
      throw new BadRequestException("Phone is required for registration");
    }
    otpService.verify(request.getEmailOtpChallengeId(), request.getEmailOtpCode(), request.getEmail(), OtpPurpose.REGISTER, "EMAIL");
    otpService.verify(request.getPhoneOtpChallengeId(), request.getPhoneOtpCode(), request.getPhone(), OtpPurpose.REGISTER, "PHONE");

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
    user.setProfileCompleted(true);
    user.setRoles(Collections.singleton(userRole));

    User saved = userRepository.save(user);
    String token = jwtService.generateToken(saved);
    UserResponse response = userService.toResponse(saved);
    auditService.log(saved, "USER_REGISTER", "User", String.valueOf(saved.getId()), requestedRole.name());
    return new AuthResponse(token, response);
  }

  public AuthResponse login(AuthRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );
    } catch (AuthenticationException ex) {
      throw new BadRequestException("Invalid credentials");
    }

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    if (user.getPhone() == null || user.getPhone().isBlank()) {
      throw new BadRequestException("Phone is not configured. Contact admin.");
    }
    otpService.verify(request.getEmailOtpChallengeId(), request.getEmailOtpCode(), user.getEmail(), OtpPurpose.LOGIN, "EMAIL");
    otpService.verify(request.getPhoneOtpChallengeId(), request.getPhoneOtpCode(), user.getPhone(), OtpPurpose.LOGIN, "PHONE");

    if (!user.isEnabledFlag()) {
      throw new BadRequestException("Account is disabled");
    }

    boolean profileComplete = user.getPhone() != null && !user.getPhone().isBlank()
        && user.getCompanyName() != null && !user.getCompanyName().isBlank()
        && user.getRoleTitle() != null && !user.getRoleTitle().isBlank();
    if (user.isProfileCompleted() != profileComplete) {
      user.setProfileCompleted(profileComplete);
      user = userRepository.save(user);
    }

    String token = jwtService.generateToken(user);
    UserResponse response = userService.toResponse(user);
    notificationService.notify(
        user,
        "Welcome back",
        "Welcome back, " + user.getFullName() + ". You are now signed in.",
        "info"
    );
    auditService.log(user, "USER_LOGIN", "User", String.valueOf(user.getId()), "Login successful");
    return new AuthResponse(token, response);
  }

  public OtpChallengeResponse sendOtp(String email, String phone, OtpPurpose purpose) {
    return otpService.sendPair(email, phone, purpose);
  }

  public OtpChallengeResponse sendLoginOtp(AuthRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );
    } catch (AuthenticationException ex) {
      throw new BadRequestException("Invalid credentials");
    }
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new BadRequestException("Invalid credentials"));
    if (user.getPhone() == null || user.getPhone().isBlank()) {
      throw new BadRequestException("Phone is not configured for this account");
    }
    return otpService.sendPair(user.getEmail(), user.getPhone(), OtpPurpose.LOGIN);
  }

  public AuthResponse googleAuth(String credential) {
    if (googleClientId == null || googleClientId.isBlank()) {
      throw new BadRequestException("Google authentication is not configured");
    }
    GoogleTokenInfo tokenInfo = fetchGoogleTokenInfo(credential);
    if (tokenInfo == null || tokenInfo.getEmail() == null || tokenInfo.getEmail().isBlank()) {
      throw new BadRequestException("Invalid Google credential");
    }
    if (!googleClientId.equals(tokenInfo.getAud())) {
      throw new BadRequestException("Google token audience mismatch");
    }
    if (!"true".equalsIgnoreCase(tokenInfo.getEmailVerified())) {
      throw new BadRequestException("Google email is not verified");
    }

    String email = tokenInfo.getEmail().trim().toLowerCase(Locale.ROOT);
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null) {
      Role role = roleRepository.findByName(RoleName.MERCHANT_OPERATIONS)
          .orElseThrow(() -> new BadRequestException("Default role not configured"));

      user = new User();
      user.setEmail(email);
      user.setFullName(resolveGoogleName(tokenInfo));
      user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID() + "Aa1!"));
      user.setCompanyName("Google Account");
      user.setRoleTitle("Merchant Operations");
      user.setProfileCompleted(false);
      user.setRoles(Collections.singleton(role));
      user = userRepository.save(user);
      auditService.log(user, "USER_REGISTER_GOOGLE", "User", String.valueOf(user.getId()), "Google signup");
    }

    if (!user.isEnabledFlag()) {
      throw new BadRequestException("Account is disabled");
    }

    String token = jwtService.generateToken(user);
    UserResponse response = userService.toResponse(user);
    notificationService.notify(
        user,
        "Welcome back",
        "Welcome back, " + user.getFullName() + ". You are now signed in with Google.",
        "info"
    );
    auditService.log(user, "USER_LOGIN_GOOGLE", "User", String.valueOf(user.getId()), "Google login");
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
      return RoleName.MERCHANT_OPERATIONS;
    }
    RoleName resolved;
    try {
      resolved = RoleName.valueOf(roleName.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("Invalid role selection");
    }
    if (!SELF_ASSIGNABLE_ROLES.contains(resolved)) {
      throw new BadRequestException("Selected role requires admin assignment");
    }
    return resolved;
  }

  private GoogleTokenInfo fetchGoogleTokenInfo(String credential) {
    try {
      return restTemplate.getForObject(
          "https://oauth2.googleapis.com/tokeninfo?id_token={idToken}",
          GoogleTokenInfo.class,
          credential
      );
    } catch (RestClientException ex) {
      throw new BadRequestException("Google credential verification failed");
    }
  }

  private String resolveGoogleName(GoogleTokenInfo tokenInfo) {
    if (tokenInfo.getName() != null && !tokenInfo.getName().isBlank()) {
      return tokenInfo.getName().trim();
    }
    String email = tokenInfo.getEmail();
    int split = email.indexOf('@');
    if (split > 0) {
      return email.substring(0, split);
    }
    return "Google User";
  }
}
