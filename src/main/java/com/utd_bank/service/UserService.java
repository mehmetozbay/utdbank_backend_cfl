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
import com.utd_bank.security.service.dto.AdminDTO;
import com.utd_bank.security.service.dto.UserDTO;

@Transactional
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private  RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private final static String USER_NO_FOUND_MSG = "user with id %d not found";

//------------REGISTER---------------------------------------------------->>>

	public void register(User user) throws BadRequestException {
		if (userRepository.existsBySSN(user.getSSN())) {
			throw new ConflictException("Error: SSN is already in use");
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

//------------GET ALL USERS---------------------------------------------------->>>

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

//	--------------------GET USER By ID---------------------------------------->>>>

	public User findById(Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NO_FOUND_MSG, id)));
		return user;
	}

//	--------------------Search--------------------------------------------------------------->>>>

	public List<User> searchUser(String firstName, String lastName, String ssn, String email) {
		return userRepository.search(firstName, lastName, email, ssn);
	}

//--------------------Delete User By ID------------------------------------------------------->>>>

	public void removeById(Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NO_FOUND_MSG, id)));

		if (user.getBuiltIn()) {
			throw new ResourceNotFoundException("You dont have permission to delete this user");
		}

		userRepository.deleteById(id);
	}

// --------------------Update User By ID-------------------------->>>>
	
	public void updateUserAuth(Long id, AdminDTO adminDTO) throws BadRequestException {
		Boolean ssnExists = userRepository.existsBySSN(adminDTO.getSSN());
		User foundUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NO_FOUND_MSG, id)));

		if (foundUser.getBuiltIn()) {
			throw new ResourceNotFoundException("You don't have permission to change this user info");
		}

		adminDTO.setBuiltIn(false);

		if (ssnExists && !adminDTO.getSSN().equals(foundUser.getSSN())) {
			throw new ConflictException("Error: SSN is in use");
		}

		if (adminDTO.getPassword() == null) {
			adminDTO.setPassword(foundUser.getPassword());
		} else {
			String encodedPassword = passwordEncoder.encode(adminDTO.getPassword());
			adminDTO.setPassword(encodedPassword);
		}

		Set<String> userRoles = adminDTO.getRoles();
		Set<Role> roles = addRoles(userRoles);

//		User user = new User( adminDTO.getFirstName(), adminDTO.getLastName(), adminDTO.getPassword(),
//				adminDTO.getPhoneNumber(), adminDTO.getEmail(), adminDTO.getAddress(), roles, adminDTO.getBuiltIn(),adminDTO.getSSN());

		foundUser.setFirstName(adminDTO.getFirstName());
		foundUser.setLastName(adminDTO.getLastName());
		foundUser.setPassword(adminDTO.getPassword());
		foundUser.setMobilePhoneNumber(adminDTO.getPhoneNumber());
		foundUser.setEmail(adminDTO.getEmail());
		foundUser.setAddress(adminDTO.getAddress());
		foundUser.setRoles(roles);
		foundUser.setBuiltIn(adminDTO.getBuiltIn());
		foundUser.setSSN(adminDTO.getSSN());
		userRepository.save(foundUser);
	}

	public  Set<Role> addRoles(Set<String> userRoles) {
		Set<Role> roles = new HashSet<>();

		if (userRoles == null) {
			Role userRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
					.orElseThrow(() -> new RuntimeException("Error: Role not found"));
			roles.add(userRole);
		} else {
			userRoles.forEach(role -> {
				switch (role) {
				case "Manager":
					Role managerRole = roleRepository.findByName(UserRole.ROLE_MANAGER)
							.orElseThrow(() -> new RuntimeException("Error: Role not found"));
					roles.add(managerRole);

					break;

				case "Employee":
					Role employeeRole = roleRepository.findByName(UserRole.ROLE_EMPLOYEE)
							.orElseThrow(() -> new RuntimeException("Error: Role not found"));
					roles.add(employeeRole);
					break;

				default:
					Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
							.orElseThrow(() -> new RuntimeException("Error: Role not found"));
					roles.add(customerRole);
				}
			});
		}
		return roles;
	}

	// --------------------Get User just for Own-------------------------->>>>
		
	public void updateUser(Long id,UserDTO userDTO) throws BadRequestException{
		boolean ssnExists = userRepository.existsBySSN(userDTO.getSSN());
		User foundUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NO_FOUND_MSG, id)));

		if (foundUser.getBuiltIn()) {
			throw new ResourceNotFoundException("You don't have permission to change this user info");
		}

		if (ssnExists && !userDTO.getSSN().equals(foundUser.getSSN())) {
			throw new ConflictException("Error: SSN is in use");
		}
		foundUser.setFirstName(userDTO.getFirstName());
		foundUser.setLastName(userDTO.getLastName());
		foundUser.setMobilePhoneNumber(userDTO.getPhoneNumber());
		foundUser.setEmail(userDTO.getEmail());
		foundUser.setAddress(userDTO.getAddress());
		userRepository.save(foundUser);
	}
	
	// --------------------Update Password-------------------------->>>>
	
	public void updatePassword(Long id,String newPassword,String oldPassword) throws BadRequestException{
		User user = userRepository.findById(id).orElseThrow(()->
		new ResourceNotFoundException("user not found with id:"+id));
		
		if(user.getBuiltIn()) {
			throw new ResourceNotFoundException("You dont have permission to update password");
		}
		
		if(!(BCrypt.hashpw(oldPassword, user.getPassword()).equals(user.getPassword()))) {
			throw new BadRequestException("Your password does not match");
		}
		
		String encodedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
		userRepository.save(user);
		
	}
	
}
