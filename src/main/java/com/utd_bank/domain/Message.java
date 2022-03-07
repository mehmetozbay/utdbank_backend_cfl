package com.utd_bank.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_messages")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
//	           
	@NotNull(message="Please enter your name")
	@NotBlank(message="Please enter not blank your name")
	@Size(min=2, max=50, message="your name musst be between {min} and {max} characters long")
	@Column(length = 100, nullable = false)
	private String name;
	
	@NotNull(message="Please enter your email")
	@NotBlank(message="Please enter not blank your email")
	@Size(min=2, max=50, message="your email musst be between {min} and {max} characters long")
	@Column(length = 100, nullable = false)
	private String email;
//	(333) 444 4444
	@Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$", message = "Please enter valid phone number")
	@Size(min = 14, max = 14, message = "Phone number should be exact 10 characters")
	@NotNull(message = "Please enter your phone number")
	@Column(nullable = false, length = 14)
	private String phoneNumber;

	@NotNull(message="Please enter your subject")
	@NotBlank(message="Please enter not blank your subject")
	@Size(min=2, max=50, message="your subject musst be between {min} and {max} characters long")
	@Column(length = 100, nullable = false)
	private String subject;
	
	@NotNull(message="Please enter your message")
	@NotBlank(message="Please enter not blank your message")
	@Size(min=2, max=50, message="your message musst be between {min} and {max} characters long")
	@Column(length = 100, nullable = false)
	private String body;
	
	
	}
