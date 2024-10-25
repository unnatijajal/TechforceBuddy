package com.authenticationservice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authenticationservice.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

	public Optional<UserEntity> findByEmail(String email);
}

