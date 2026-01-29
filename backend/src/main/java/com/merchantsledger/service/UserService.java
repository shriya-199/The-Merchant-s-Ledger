package com.merchantsledger.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.merchantsledger.dto.UserResponse;
import com.merchantsledger.dto.UserUpdateRequest;
import com.merchantsledger.entity.User;
import com.merchantsledger.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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
        user.isEnabledFlag()
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
}
