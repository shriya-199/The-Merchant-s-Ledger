package com.merchantsledger.seed;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

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
  private final JdbcTemplate jdbcTemplate;

  public DataSeeder(RoleRepository roleRepository,
                    UserRepository userRepository,
                    PasswordEncoder passwordEncoder,
                    JdbcTemplate jdbcTemplate) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(String... args) {
    syncRoleCheckConstraint();
    seedRoles();
    seedAdmin();
  }

  private void syncRoleCheckConstraint() {
    String allowedRoles = Arrays.stream(RoleName.values())
        .map(RoleName::name)
        .map(role -> "'" + role + "'")
        .collect(Collectors.joining(","));

    jdbcTemplate.execute("ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_name_check");
    jdbcTemplate.execute("ALTER TABLE roles ADD CONSTRAINT roles_name_check CHECK (name IN (" + allowedRoles + "))");
  }

  private void seedRoles() {
    Arrays.stream(RoleName.values()).forEach(roleName -> {
      if (roleRepository.findByName(roleName).isEmpty()) {
        roleRepository.save(new Role(roleName));
      }
    });
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
