package com.merchantsledger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.merchantsledger.dto.AdminUserUpdateRequest;
import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.entity.Role;
import com.merchantsledger.entity.RoleName;
import com.merchantsledger.entity.User;
import com.merchantsledger.exception.BadRequestException;
import com.merchantsledger.exception.NotFoundException;
import com.merchantsledger.repository.RoleRepository;
import com.merchantsledger.repository.UserRepository;

@Service
public class AdminService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserService userService;
  private final AuditService auditService;

  public AdminService(UserRepository userRepository, RoleRepository roleRepository, UserService userService, AuditService auditService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.userService = userService;
    this.auditService = auditService;
  }

  public List<UserResponse> listUsers(User actor) {
    String tenantKey = TenantResolver.resolveTenantKey(actor);
    return userRepository.findAll().stream()
        .filter(user -> tenantKey.equals(TenantResolver.resolveTenantKey(user)))
        .map(userService::toResponse)
        .collect(Collectors.toList());
  }

  public UserResponse updateUser(User actor, Long id, AdminUserUpdateRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (request.getEnabled() != null) {
      user.setEnabledFlag(request.getEnabled());
    }

    if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
      RoleName roleName;
      try {
        roleName = RoleName.valueOf(request.getRoleName().trim().toUpperCase());
      } catch (IllegalArgumentException ex) {
        throw new BadRequestException("Invalid role");
      }
      Role role = roleRepository.findByName(roleName)
          .orElseThrow(() -> new BadRequestException("Role not found"));
      user.getRoles().clear();
      user.getRoles().add(role);
    }

    User saved = userRepository.save(user);
    auditService.log(actor, "USER_ADMIN_UPDATE", "User", String.valueOf(saved.getId()), request.getRoleName());
    return userService.toResponse(saved);
  }
}
