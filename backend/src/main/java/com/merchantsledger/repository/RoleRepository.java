package com.merchantsledger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.Role;
import com.merchantsledger.entity.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(RoleName name);
}
