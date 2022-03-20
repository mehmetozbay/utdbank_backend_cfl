package com.utd_bank.service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utd_bank.domain.Account;
import com.utd_bank.domain.AccountModifyInformation;
import com.utd_bank.domain.AccountNumber;
import com.utd_bank.domain.Role;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.AccountStatusType;
import com.utd_bank.domain.enumeration.UserRole;
import com.utd_bank.exception.BadRequestException;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.repository.AccModifyInformationRepository;
import com.utd_bank.repository.AccountNumberRepository;
import com.utd_bank.repository.AccountRepository;
import com.utd_bank.repository.RoleRepository;
import com.utd_bank.repository.UserRepository;
import com.utd_bank.security.service.dto.AccountDTO;
import com.utd_bank.security.service.dto.AdminAccountDTO;

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
	
	private  RoleRepository roleRepository;
//		--------------------CREATE AN ACCOUNT For Employee and Manager------------------------>>>>

	public Long createAccountJustOwn(Long id, Account account) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		String createdBy = accountModifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(),
				user.getRole());
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
		if (acc.getAccountStatusType().equals(AccountStatusType.ACTIVE)) {
			accModifyInformationRepository.save(accountModifyInformation);
			accountRepository.save(account);
		} else
			throw new BadRequestException(
					String.format("You dont have active account with accountNo %d to update", accountNo));
	}

	
	
	
	// *********************************************************************

	// ========================AUTH====================================>>>>>>

	// *********************************************************************

	
	
	
	// --------------------Create an Account by User ID-------------->>>>>

	@SuppressWarnings("unlikely-arg-type")
	public Long addAuth(Long id, Long userId, Account account) throws BadRequestException {
		User admin = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));

		String createdBy = accountModifyInformation.setModifiedBy(admin.getFirstName(), admin.getLastName(),
				admin.getRole());

		Timestamp createdDate = accountModifyInformation.setDate();

		AccountModifyInformation accountModifyInformation = new AccountModifyInformation(createdBy, createdDate,
				createdBy, createdDate);

		Set<String> adminRoles = admin.getRoles();
	
		Set<String> userRoles = admin.getRoles();
	
		accModifyInformationRepository.save(accountModifyInformation);
		account.setAccModInfId(accountModifyInformation);
		account.setUserId(user);
		account.setAccountStatusType(AccountStatusType.ACTIVE);
		AccountNumber accountNumber = new AccountNumber();
		if (adminRoles.contains("Manager") || userRoles.contains("Customer")) {
			accountNumberRepository.save(accountNumber);
			account.setAccountNo(accountNumber);
			accountRepository.save(account);
		}
		else
			throw new BadRequestException(
					String.format("You dont have permission to create " + "user with userId %d", userId));

		return account.getAccountNo().getId();
	}

	// --------------------Get All Accounts-------------->>>>>

	public List<AdminAccountDTO> fetchAllAccounts(Long id) {
		User admin = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		Set<String> adminRoles = admin.getRoles();
	
		if (adminRoles.contains("Manager"))
			return accountRepository.findAllByOrderById();
		else
			return accountRepository.findAllByRole(UserRole.ROLE_CUSTOMER);
	}

	// --------------------Get Account By UserId-------------->>>>>

	public List<AdminAccountDTO> findAllByUserId(Long id, Long userId) throws ResourceNotFoundException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
		User admin = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		Set<String> adminRoles = admin.getRoles();
		Set<String> userRoles = admin.getRoles();
		if (adminRoles.contains("Manager") || userRoles.contains("Customer")) 
			
			return accountRepository.findAllByUserId(user);
		else
			throw new BadRequestException(
					String.format("You dont have permission to access account " + "with userId %d", userId));
	}
	
	
	//--------------------Get Account By Account No-------------->>>>>
	
	 public AdminAccountDTO findByAccountNoAuth(Long id, Long accountNo) throws ResourceNotFoundException {
		 User admin = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
			
	        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));

	        AdminAccountDTO account = accountRepository.findByAccountNoOrderById(accountNumber)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));

	        User user = userRepository.findById(account.getUserId())
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,
	                        account.getUserId())));

	        Set<String> adminRoles = admin.getRoles();
			Set<String> userRoles = admin.getRoles();
			if (adminRoles.contains("Manager") || userRoles.contains("Customer")) 
				     return account;

	        else
	            throw new BadRequestException(String.format("You dont have permission to access account " +
	                    "with accountNo %d", accountNo));
	    }
	 
	 
	  //--------------------Delete Account By Account No-------------->>>>>
	 
	 public void removeByAccountIdAuth(Long id, Long accountNo) throws ResourceNotFoundException {
		 User admin = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
	        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        Account account = accountRepository.findByAccountNo(accountNumber)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        User user = userRepository.findById(account.getUserId().getId()).orElseThrow(() ->
	                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, account.getUserId().getId())));
	        Set<String> adminRoles = admin.getRoles();
			Set<String> userRoles = admin.getRoles();
			if (adminRoles.contains("Manager") ||adminRoles.contains("Employee") || userRoles.contains("Customer")) 
			{
	                accModifyInformationRepository.deleteById(account.getAccModInfId().getId());
	                accountRepository.deleteById(account.getId());
	        }
	        else
	            throw new BadRequestException("You don't have permission to delete account!");
	    }
	 
	 
	    //--------------------Update Account By Account No-------------->>>>>
	 
	 public void updateAccountAuth(Long id, Long accountNo, Account account) throws BadRequestException {
		 User admin = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
	        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        Account acc = accountRepository.findByAccountNo(accountNumber)
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
	        User user = userRepository.findById(acc.getUserId().getId())
	                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,
	                        acc.getUserId().getId())));
	        Set<String> adminRoles = admin.getRoles();
			Set<String> userRoles = admin.getRoles();
	        Timestamp closedDate = null;
	        if (account.getAccountStatusType().equals(AccountStatusType.CLOSED))
	            closedDate = accountModifyInformation.setDate();
	        String lastModifiedBy = accountModifyInformation.setModifiedBy(admin.getFirstName(), admin.getLastName(),
	                admin.getRole());
	        Timestamp lastModifiedDate = accountModifyInformation.setDate();
	        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(acc.getAccModInfId().getId(),
	                lastModifiedBy, lastModifiedDate, closedDate);
	        accModifyInformationRepository.save(accountModifyInformation);
	        account.setUserId(acc.getUserId());
	        account.setId(acc.getId());
	        account.setAccModInfId(accountModifyInformation);
	        account.setAccountNo(accountNumber);
	        if (adminRoles.contains("Manager") || userRoles.contains("Customer")) 
	        	        accountRepository.save(account);
	        else
	            throw new BadRequestException(String.format("You dont have permission to update " +
	                    "account with accountNo %d", accountNo));
	    }
	 

}
