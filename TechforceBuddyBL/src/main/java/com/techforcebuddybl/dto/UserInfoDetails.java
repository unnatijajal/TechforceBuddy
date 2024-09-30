package com.techforcebuddybl.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techforcebuddybl.entity.UserEntity;

/*
 * This is class which is implements the USerDetails class to get the username
 * password and roles of user etc. for JWT authentication.  
 */

@SuppressWarnings("serial")
public class UserInfoDetails implements UserDetails {
	
	private String username ;
	private String password;
	private List<GrantedAuthority> authorities;
	
	public UserInfoDetails(UserEntity user) {
		username = user.getEmail();
		password = user.getPassword();
		authorities = List.of(user.getRole().split(","))
				.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return username;
	}
	
	
}
