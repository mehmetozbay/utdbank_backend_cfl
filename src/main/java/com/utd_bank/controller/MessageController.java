package com.utd_bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping
	public ResponseEntity<List<Message>> getAllMessage(){
	List<Message> allMessage=	messageService.getAllMessage();
//	return new ResponseEntity<>(allMessage,HttpStatus.OK);
	return  ResponseEntity.ok(allMessage);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Message> getMessageById(@PathVariable Long id){
	Message msg=	messageService.getMessage(id);
	return  ResponseEntity.ok(msg);
	}
	
	 @GetMapping("/request")
	 public Message getMessageByRequest(@RequestParam Long id) {
		 return messageService.getMessage(id);
	 }
	 
	 @DeleteMapping("/{id}")
	public ResponseEntity<Map<String,String>> deleteMessage(@PathVariable Long id){
		 messageService.deleteMessage(id);
		 Map<String,String> map=new HashMap<>();
		 map.put("succes", String.valueOf(true));
		 map.put("id", String.valueOf(id.longValue()));
		 return new ResponseEntity<>(map,HttpStatus.OK);
	 }
	
	 @PutMapping("/{id}")
	 public ResponseEntity<Message> updateMessage(@Valid @PathVariable Long id, @RequestBody Message newmessage){
		 Message updatedMessage = messageService.updatedMessage(id,newmessage);
		 return new ResponseEntity<>(updatedMessage,HttpStatus.OK);
	 }
	 
	
}
