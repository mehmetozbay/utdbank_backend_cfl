package com.utd_bank.security.service.dto;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utd_bank.domain.Role;
import com.utd_bank.domain.enumeration.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 
 * @author devops
 *
 */
public class AdminDTO {
	@NotBlank(message="Please provide not blank firstName")
	@NotNull(message="Please provide your firstName")
	@Size(min=1,max=15,message="FirstName '${validatedValue}' must be between {min} and {max} chracters long")
	private String firstName;
	
	@NotBlank(message="Please provide not blank lastName")
	@NotNull(message="Please provide your lastName")
	@Size(min=1,max=15,message="lastName '${validatedValue}' must be between {min} and {max} chracters long")
	private String lastName;
	
	@Email(message="Please provide a valid email")
	@NotNull(message="Please provide your email")
	@Size(min=5,max=100,message="Email '${validatedValue}' must be between {min} and {max} chracters long")
	private String email;
	
	@JsonIgnore
	private String password;
	
	// (555) 555 5555
	// 555-555-5555
	// 555.555.5555
	@Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please provide valid phone number")
	private String phoneNumber;
	
	@NotBlank(message="Please provide not blank Adress")
	@NotNull(message="Please provide your Address")
	@Size(min=10,max=250,message="Adress '${validatedValue}' must be between {min} and {max} chracters long")
	private String address;
	

	
	@Pattern(regexp = "^(\\d{3}-?\\d{2}-?\\d{4}|XXX-XX-XXXX)$",
            message = "Please provide valid SSN")
	@Column(length=15,nullable=false)
	private String SSN;
	
	@Column(nullable = false)
	private Boolean builtIn;

	private Set<String> roles;
	



	
	

	
	
}
