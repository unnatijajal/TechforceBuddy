package com.authenticationservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authenticationservice.dto.UserInfoDetails;
import com.authenticationservice.entity.UserEntity;
import com.authenticationservice.repo.UserRepository;

/*
 * This is the custom UserDetailService class which have method to load the user details.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	
	// Load the user detail from the DB when given email id is matched.
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Optional<UserEntity> userDetail = userRepository.findByEmail(email);
		
		return userDetail.map(UserInfoDetails::new)
				.orElseThrow(()->new UsernameNotFoundException("User not found"));
	}
	
	

}