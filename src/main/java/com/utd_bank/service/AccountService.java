package com.utd_bank.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.utd_bank.domain.Account;
import com.utd_bank.domain.AccountModifyInformation;
import com.utd_bank.domain.AccountNumber;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.AccountStatusType;
import com.utd_bank.exception.BadRequestException;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.repository.AccModifyInformationRepository;
import com.utd_bank.repository.AccountNumberRepository;
import com.utd_bank.repository.AccountRepository;
import com.utd_bank.repository.UserRepository;
import com.utd_bank.security.service.dto.AccountDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

	private final static String ACCOUNT_NOT_FOUND_MSG = "account with accountNo %d not found";
	private final static String SSN_NOT_FOUND_MSG = "account with ssn %s not found";
	private final static String USER_NOT_FOUND_MSG = "user with id %d not found";
	private AccountRepository accountRepository;
	private AccountNumberRepository accountNumberRepository;
	private final AccModifyInformationRepository accModifyInformationRepository;
	private UserRepository userRepository;
	private final AccountModifyInformation accountModifyInformation = new AccountModifyInformation();

//		--------------------CREATE AN ACCOUNT For Employee and Manager------------------------>>>>

	public Long createAccountJustOwn(Long id, Account account) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		  String createdBy = accountModifyInformation.setModifiedBy(user.getFirstName(),
	                user.getLastName(), user.getRole());
	        Timestamp createdDate = accountModifyInformation.setDate();
	        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(createdBy, createdDate,
	                createdBy, createdDate);
	        accModifyInformationRepository.save(accountModifyInformation);
	        account.setAccModInfId(accountModifyInformation);
	        account.setUserId(user);
	        account.setAccountStatusType(AccountStatusType.ACTIVE);
	        AccountNumber accountNumber = new AccountNumber();
	        accountNumberRepository.save(accountNumber);
	        account.setAccountNo(accountNumber);
	        accountRepository.save(account);
	        return account.getAccountNo().getId();
	}

//		--------------------Get Account By Account No Just for Own------------------------>>>>

	public Account findByIDAccountNo(Long accountNo, Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
		Account accountDao = accountRepository.findByAccountNoAndUserIdOrderById(accountNumber, user)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
		if (accountDao.getAccountStatusType().equals(AccountStatusType.ACTIVE))
			return accountDao;
		else
			throw new BadRequestException(String.format("You dont have active account with accountNo %d", accountNo));
	}

//		--------------------Get Accounts Just for Own------------------------>>>>

	public List<AccountDTO> findAllByID(Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

		return accountRepository.findAllByUserIdAndAccountStatusType(user, AccountStatusType.ACTIVE);
	}

//		--------------------Remove Account By Account No Just for Own (Employee Manager)------------->>>>
	
	 public void removeByAccountId(Long id, Long accountNo) throws BadRequestException {
		 User user = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
	        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        Account account = accountRepository.findByAccountNoAndUserId(accountNumber, user)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	            accModifyInformationRepository.deleteById(account.getAccModInfId().getId());
	            accountRepository.deleteById(account.getId());
	    }
	 
//		--------------------Update Account By Account No Just for Own (Employee Manager)------------->>>>
	 
	 public void updateAccount(Long id, Long accountNo, Account account) throws BadRequestException {
		 User user = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
	        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        Account acc = accountRepository.findByAccountNoAndUserId(accountNumber, user)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        Timestamp closedDate = null;
	        if (account.getAccountStatusType().equals(AccountStatusType.CLOSED))
	            closedDate = accountModifyInformation.setDate();
	        String lastModifiedBy = accountModifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(),
	                user.getRole());
	        Timestamp lastModifiedDate = accountModifyInformation.setDate();
	        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(acc.getAccModInfId().getId(),
	                lastModifiedBy, lastModifiedDate, closedDate);
	        account.setUserId(user);
	        account.setId(acc.getId());
	        account.setAccModInfId(accountModifyInformation);
	        account.setAccountNo(accountNumber);
	        account.setBalance(acc.getBalance());
	        if (acc.getAccountStatusType().equals(AccountStatusType.ACTIVE)){
	            accModifyInformationRepository.save(accountModifyInformation);
	            accountRepository.save(account);
	        }
	        else
	            throw new BadRequestException(String.format(
	                    "You dont have active account with accountNo %d to update", accountNo));
	    }


	 

}
