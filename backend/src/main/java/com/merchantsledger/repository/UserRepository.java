package com.merchantsledger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merchantsledger.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
}
