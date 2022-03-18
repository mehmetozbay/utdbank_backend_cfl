package com.utd_bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utd_bank.domain.Account;
import com.utd_bank.domain.AccountNumber;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.AccountStatusType;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.security.service.dto.AccountDTO;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByAccountNoAndUserIdOrderById(AccountNumber accountNo, User user)
			throws ResourceNotFoundException;

	List<AccountDTO> findAllByUserIdAndAccountStatusType(User user, AccountStatusType accountStatusType)
			throws ResourceNotFoundException;

	Optional<Account> findByAccountNoAndUserId(AccountNumber accountNo, User userId) throws ResourceNotFoundException;

}
