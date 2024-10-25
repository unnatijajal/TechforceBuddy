package com.authenticationservice.services;

import java.util.Optional;

import com.authenticationservice.entity.UserEntity;

public interface UserService {

	public UserEntity saveUser(UserEntity userEntity);
	
	public Optional<UserEntity> findByEmail(String email);
}
