package com.utd_bank.security.service;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.utd_bank.domain.User;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String SSN) throws UsernameNotFoundException {
		User user=userRepository.findBySSN(SSN).orElseThrow(()->new ResourceNotFoundException
				("User not found with SSN:"+SSN));
		return UserDetailsImpl.build(user);		
	}

}
