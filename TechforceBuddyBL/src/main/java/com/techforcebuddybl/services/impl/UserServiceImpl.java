package com.techforcebuddybl.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techforcebuddybl.entity.UserEntity;
import com.techforcebuddybl.repo.UserRepository;
import com.techforcebuddybl.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserEntity saveUser(UserEntity userEntity) {
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		return userRepository.save(userEntity);
	}

	@Override
	public Optional<UserEntity> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
