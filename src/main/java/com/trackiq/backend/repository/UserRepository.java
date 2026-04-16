package com.trackiq.backend.repository;

import com.trackiq.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.trackiq.backend.enums.Role;
import java.util.Optional;
import java.util.*;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 🔹 Find user by email (used in login)
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    // 🔹 Check if email already exists (used in signup)
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);

}