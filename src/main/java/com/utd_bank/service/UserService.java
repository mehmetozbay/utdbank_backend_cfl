package com.utd_bank.service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.utd_bank.domain.Role;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.UserRole;
import com.utd_bank.exception.BadRequestException;
import com.utd_bank.exception.ConflictException;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.repository.RoleRepository;
import com.utd_bank.repository.UserRepository;

@Transactional
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final static String USER_NO_FOUND_MSG = "user with id %d not found";

	public void register(User user) throws BadRequestException {

		if (userRepository.existsBySSN(user.getSSN())) {
			throw new ConflictException("Error: Email is already in use");
		}

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPassword);
		user.setBuiltIn(false);

		Set<Role> roles = new HashSet<>();
		Role role = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
				.orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);

	}

	
	
	
}
