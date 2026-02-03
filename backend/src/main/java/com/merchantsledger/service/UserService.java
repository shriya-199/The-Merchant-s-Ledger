package com.merchantsledger.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.merchantsledger.dto.CompleteProfileRequest;
import com.merchantsledger.dto.DeleteAccountRequest;
import com.merchantsledger.dto.OtpChallengeResponse;
import com.merchantsledger.dto.OtpPurpose;
import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.dto.UserUpdateRequest;
import com.merchantsledger.entity.Role;
import com.merchantsledger.entity.RoleName;
import com.merchantsledger.entity.User;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.repository.RoleRepository;
import com.merchantsledger.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
  private static final Set<RoleName> SELF_ASSIGNABLE_ROLES = Set.of(
      RoleName.MERCHANT_OPERATIONS,
      RoleName.MERCHANT_FINANCE,
      RoleName.MERCHANT_VIEWER,
      RoleName.PICKER_PACKER,
      RoleName.RECEIVER_GRN_OPERATOR,
      RoleName.STAFF,
      RoleName.MANAGER,
      RoleName.USER
  );

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final OtpService otpService;
  private final AuditService auditService;

  public UserService(UserRepository userRepository,
                     RoleRepository roleRepository,
                     OtpService otpService,
                     AuditService auditService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.otpService = otpService;
    this.auditService = auditService;
  }

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  public UserResponse toResponse(User user) {
    Set<String> roles = user.getRoles().stream()
        .map(role -> role.getName().name())
        .collect(Collectors.toSet());
    return new UserResponse(
        user.getId(),
        user.getFullName(),
        user.getEmail(),
        roles,
        user.getCompanyName(),
        user.getPhone(),
        user.getAddress(),
        user.getRoleTitle(),
        user.isEnabledFlag(),
        user.isProfileCompleted()
    );
  }

  public UserResponse updateProfile(User user, UserUpdateRequest request) {
    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      user.setFullName(request.getFullName());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
    if (request.getCompanyName() != null) {
      user.setCompanyName(request.getCompanyName());
    }
    if (request.getAddress() != null) {
      user.setAddress(request.getAddress());
    }
    if (request.getRoleTitle() != null) {
      user.setRoleTitle(request.getRoleTitle());
    }
    User saved = userRepository.save(user);
    return toResponse(saved);
  }

  public UserResponse completeProfile(User user, CompleteProfileRequest request) {
    user.setFullName(request.getFullName().trim());
    user.setPhone(request.getPhone().trim());
    user.setCompanyName(request.getCompanyName().trim());
    user.setAddress(request.getAddress() == null ? null : request.getAddress().trim());
    user.setRoleTitle(request.getRoleTitle().trim());

    RoleName roleName;
    try {
      roleName = RoleName.valueOf(request.getRoleName().trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("Invalid role selection");
    }

    if (!SELF_ASSIGNABLE_ROLES.contains(roleName)) {
      throw new BadRequestException("Selected role requires admin assignment");
    }
    Role role = roleRepository.findByName(roleName)
        .orElseThrow(() -> new BadRequestException("Role not configured"));
    user.getRoles().clear();
    user.getRoles().add(role);

    if (user.getPhone().isBlank() || user.getCompanyName().isBlank() || user.getRoleTitle().isBlank()) {
      throw new BadRequestException("Phone, company, and role title are required");
    }
    user.setProfileCompleted(true);
    User saved = userRepository.save(user);
    return toResponse(saved);
  }

  public OtpChallengeResponse sendDeleteAccountOtp(User user) {
    if (user.getPhone() == null || user.getPhone().isBlank()) {
      throw new BadRequestException("Phone is required before account deletion");
    }
    return otpService.sendPhoneOnly(user.getPhone(), OtpPurpose.ACCOUNT_DELETE);
  }

  public void deleteAccount(User user, DeleteAccountRequest request) {
    if (user.getPhone() == null || user.getPhone().isBlank()) {
      throw new BadRequestException("Phone is required before account deletion");
    }
    otpService.verify(
        request.getPhoneOtpChallengeId(),
        request.getPhoneOtpCode(),
        user.getPhone(),
        OtpPurpose.ACCOUNT_DELETE,
        "PHONE"
    );
    auditService.log(user, "USER_DELETE_SELF", "User", String.valueOf(user.getId()), "Self account deletion");
    userRepository.delete(user);
  }
}
