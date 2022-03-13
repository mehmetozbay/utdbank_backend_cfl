package com.utd_bank.controller.vm;


import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginVM {
	
	@Pattern(regexp = "^(\\d{3}-?\\d{2}-?\\d{4}|XXX-XX-XXXX)$",
            message = "Please provide valid SSN")
	@Column(length=15,nullable=false)
	private String SSN;
	
	
	@NotNull(message="Please provide your password")
	@NotBlank(message="Please provide your password")
	@Size(min=4,max=60,message="Password '${validatedValue}' must be between {min} and {max} chracters long")
	private String password;
	
	
	@Override
	public String toString() {
	return "LoginVM{"+
			"SSN="+SSN+
			"}";
	}
	
}
