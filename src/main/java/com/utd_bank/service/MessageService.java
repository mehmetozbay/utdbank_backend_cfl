package com.utd_bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utd_bank.domain.Message;
import com.utd_bank.repository.MessageRepository;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;
	public Message createMessage(Message message) {
		
		Message msg=messageRepository.save(message);
		return msg;
	}
}
