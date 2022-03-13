package com.utd_bank.controller;


import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.utd_bank.controller.vm.LoginVM;
import com.utd_bank.domain.User;
import com.utd_bank.security.JwtUtils;
import com.utd_bank.service.UserService;


@RestController
@RequestMapping
public class UserJWTController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@PostMapping("/register")
	public ResponseEntity<Map<String,Boolean>> registerUser(@Valid @RequestBody User user){
		userService.register(user);
		Map <String,Boolean> map=new HashMap<>();
		map.put("Register is Ok", true);
		return new ResponseEntity<>(map,HttpStatus.CREATED);
		//RegisterOK ok=new RegisterOK("Successfull");
		//return new ResponseEntity<>(ok,HttpStatus.CREATED);			
	}
	

	@PostMapping("/login")
	public ResponseEntity<JWTToken> login(@Valid @RequestBody LoginVM loginVM){
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginVM.getSSN(), loginVM.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt=jwtUtils.generateToken(authentication);
		
		HttpHeaders httpHeaders=new HttpHeaders();
		httpHeaders.add("Authorization","Bearer "+jwt);
		return new ResponseEntity<>(new JWTToken(jwt),httpHeaders,HttpStatus.OK);
	}
	
	static class JWTToken{
		private String token;
		
		public JWTToken(String token) {
			this.token=token;
		}
		
		@JsonProperty("jwt_token")
		String getToken() {
			return token;
		}
		
		void setToken(String token) {
			this.token=token;
		}
	}
	
	
/*	static class RegisterOK{
		
		private String message;
		
		RegisterOK(String message){
			this.setMessage(message);
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	
	}*/
	
}
