package com.utd_bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utd_bank.domain.User;
import com.utd_bank.security.service.dto.AdminDTO;
import com.utd_bank.security.service.dto.UserDTO;
import com.utd_bank.service.UserService;

@RestController
//@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
	private UserService userService;
	private ModelMapper modelMapper;

	public UserController(UserService userService, ModelMapper modelMapper) {
		this.userService = userService;
		this.modelMapper = modelMapper;
	}

//	--------------------GET ALL USER------------------------------------------------------------------>>>>

	@GetMapping("/auth/all")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> userList = userService.getAllUsers();
		List<UserDTO> userDTOList = userList.stream().map(this::convertToDTO).collect(Collectors.toList());
		return new ResponseEntity<>(userDTOList, HttpStatus.OK);
	}

//	--------------------GET USER By ID--------------------------------------------------------------->>>>

	@GetMapping("/{id}/auth")
	@PreAuthorize(" hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<UserDTO> getUserByIdAdmin(@PathVariable Long id) {
		User user = userService.findById(id);
		UserDTO userDTO = convertToDTO(user);
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

//	--------------------Search--------------------------------------------------------------->>>>
	
	@GetMapping("/search")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<List<UserDTO>> searchUser(@RequestParam("firstname") String firstName,
			@RequestParam("lastname") String lastName, @RequestParam("ssn") String ssn,
			@RequestParam("email") String email) {
		List<User> userList = userService.searchUser(firstName, lastName, ssn, email);
		List<UserDTO> userDTOList = userList.stream().map(this::convertToDTO).collect(Collectors.toList());
		return new ResponseEntity<>(userDTOList, HttpStatus.OK);
	}

// --------------------Delete User By ID------------------------------------------------>>>>
	
	@DeleteMapping("/{id}/auth")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<Map<String, Boolean>>deleteUser(@PathVariable Long id){
		userService.removeById(id);
		
		Map<String, Boolean> map = new HashMap<>();
		map.put("User deleted successfully", true);
		return new ResponseEntity<>(map,HttpStatus.OK);
	}

	
	// --------------------Update User By Id------------------------------------------------>>>>
	
	@PutMapping("/{id}/auth")
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<Map<String, Boolean>> updateUserAuth(@PathVariable Long id,
			@Valid @RequestBody AdminDTO adminDTO){
		userService.updateUserAuth(id, adminDTO);
		Map<String, Boolean> map = new HashMap<>();
		map.put("User updated successfully", true);
		return new ResponseEntity<>(map,HttpStatus.OK);
	}
	
	
	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		return userDTO;
	}
	
	
	
	// --------------------Get User just for Own-------------------------->>>>
	@GetMapping()
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<UserDTO> getUser(HttpServletRequest request) {
		Long id=(Long)request.getAttribute("id");  
		User user = userService.findById(id);
		UserDTO userDTO = convertToDTO(user);
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}
	// --------------------Update User just for Own-------------------------->>>>
	
	@PutMapping("/update")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<Map<String, Boolean>> updateUser(HttpServletRequest request,
			@Valid @RequestBody UserDTO userDTO){
		Long id=(Long)request.getAttribute("id");  
		userService.updateUser(id,userDTO);
		Map<String, Boolean> map = new HashMap<>();
		map.put("User updated successfully", true);
		return new ResponseEntity<>(map,HttpStatus.OK);
	}
	
	
	// --------------------Update Password-------------------------->>>>
	
	@PatchMapping("/password")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('EMPLOYEE') or hasRole('MANAGER')")
	public ResponseEntity<Map<String, Boolean>> updatePassword(HttpServletRequest request,
			@RequestBody Map<String,String> userMap){
		Long id=(Long)request.getAttribute("id");
		String newPassword = userMap.get("newPassword");
		String oldPassword = userMap.get("oldPassword");
		userService.updatePassword(id,newPassword,oldPassword);
		Map<String, Boolean> map = new HashMap<>();
		map.put("Password changed successfully", true);
		return new ResponseEntity<>(map,HttpStatus.OK);
	}
}
