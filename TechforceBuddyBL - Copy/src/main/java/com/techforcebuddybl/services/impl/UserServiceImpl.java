package com.techforcebuddybl.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.entity.UserEntity;
import com.techforcebuddybl.repo.UserRepository;
import com.techforcebuddybl.services.UserService;

/*
 * This class is the implementation of the UserService.
 * Which have different method to save and get the user from the 
 * database.
 */

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// Save the user details into the database.
	@Override
	public UserEntity saveUser(UserEntity userEntity) {
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		return userRepository.save(userEntity);
	}

	// Fetch the user details from the database which email id is match to given email id.
	@Override
	public Optional<UserEntity> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
