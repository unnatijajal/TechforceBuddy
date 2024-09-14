package com.techforcebuddybl.services;

import java.util.Optional;

import com.techforcebuddybl.entity.UserEntity;

public interface UserService {

	public UserEntity saveUser(UserEntity userEntity);
	
	public Optional<UserEntity> findByEmail(String email);
}
