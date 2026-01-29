package com.merchantsledger.seed;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.merchantsledger.entity.Role;
import com.merchantsledger.entity.RoleName;
import com.merchantsledger.entity.User;
import com.merchantsledger.repository.RoleRepository;
import com.merchantsledger.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public DataSeeder(RoleRepository roleRepository,
                    UserRepository userRepository,
                    PasswordEncoder passwordEncoder) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    seedRoles();
    seedAdmin();
  }

  private void seedRoles() {
    if (roleRepository.count() == 0) {
      roleRepository.save(new Role(RoleName.ADMIN));
      roleRepository.save(new Role(RoleName.USER));
      roleRepository.save(new Role(RoleName.MANAGER));
      roleRepository.save(new Role(RoleName.STAFF));
    }
  }

  private void seedAdmin() {
    if (userRepository.existsByEmail("admin@ledger.com")) {
      return;
    }

    Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();

    User admin = new User();
    admin.setFullName("Ledger Admin");
    admin.setEmail("admin@ledger.com");
    admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
    admin.setCompanyName("Merchant's Ledger");
    admin.setRoleTitle("Administrator");
    admin.setRoles(Set.of(adminRole));
    userRepository.save(admin);
  }

}
