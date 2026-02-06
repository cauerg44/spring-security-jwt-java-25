package com.security.test.api.infra.repository;

import com.security.test.api.infra.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByLogin(String username);
    boolean existsByLogin(String login);
}
