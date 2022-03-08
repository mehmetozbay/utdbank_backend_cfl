package com.utd_bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utd_bank.domain.Message;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.repository.MessageRepository;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;
	public Message createMessage(Message message) {
		
		Message msg=messageRepository.save(message);
		return msg;
	}
	
	
	public List<Message> getAllMessage(){
		return messageRepository.findAll();
	}
	
	
	public Message getMessage(Long id) {
		Message foundMessage=messageRepository.findById(id)
				.orElseThrow(()->new ResourceNotFoundException(id +" Message not found"));
		return foundMessage;
	}
	
	public void deleteMessage(Long id) throws ResourceNotFoundException{
		messageRepository.deleteById(id);
	}
	
	
	public Message updatedMessage(Long id, Message newMessage) {
		Message foundMessage=getMessage(id);
		foundMessage.setBody(newMessage.getBody());
		foundMessage.setName(newMessage.getName());
		foundMessage.setSubject(newMessage.getSubject());
		return messageRepository.save(foundMessage);
	}
}
