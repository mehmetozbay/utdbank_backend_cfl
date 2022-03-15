package com.utd_bank.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.utd_bank.domain.User;
import com.utd_bank.exception.BadRequestException;
import com.utd_bank.exception.ConflictException;
import com.utd_bank.exception.ResourceNotFoundException;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findBySSN(String SSN) throws ResourceNotFoundException;	
	Boolean existsBySSN(String SSN) throws ConflictException;
	List<User> findByLastNameStartingWith(String lastName);

	   @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1%"
	            + " OR u.lastName LIKE %?2%"
	            + " OR u.email LIKE %?3%"
	            + " OR u.SSN LIKE %?3%")
	    public List<User> search(String firstName, String lastName, String email, String SSN);
}
