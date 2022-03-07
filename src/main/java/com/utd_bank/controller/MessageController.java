package com.utd_bank.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utd_bank.domain.Message;
import com.utd_bank.service.MessageService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/message")
@AllArgsConstructor
public class MessageController {

//	@Autowired
	private MessageService messageService;
	@PostMapping
	public ResponseEntity<Message> createMessage(@Valid @RequestBody Message message ){
		
	Message msg=messageService.createMessage(message);
	
		return new ResponseEntity<>(msg,HttpStatus.CREATED);
	}
	
}
