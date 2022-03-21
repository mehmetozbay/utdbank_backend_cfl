package com.utd_bank.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.utd_bank.domain.Account;
import com.utd_bank.domain.AccountNumber;
import com.utd_bank.domain.Transfer;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.AccountStatusType;
import com.utd_bank.domain.enumeration.UserRole;
import com.utd_bank.exception.BadRequestException;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.repository.AccountNumberRepository;
import com.utd_bank.repository.AccountRepository;
import com.utd_bank.repository.TransferRepository;
import com.utd_bank.repository.UserRepository;
import com.utd_bank.security.service.dto.TransferAdminDTO;
import com.utd_bank.security.service.dto.TransferDTO;
import com.utd_bank.security.service.dto.TransferUserDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransferService {

	private final AccountRepository accountRepository;
	private final AccountNumberRepository accountNumberRepository;
	private final TransferRepository transferRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	private final static String USER_NOT_FOUND_MSG = "user with id %d not found";
	private final static String ACCOUNT_NOT_FOUND_MSG = "account with accountNo %s not found";
	private final static String TRANSFER_NOT_FOUND_MSG = "transfer with id %d not found";

//  ***********************************************************************

//							JUST FOR OWN

//	***********************************************************************

// ---------------Create Transfer ---------------------------------------->>>

	public Double create(Long id, TransferDTO transfer) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		Account fromAccount = accountRepository.findByAccountNoAndUserId(transfer.getFromAccountId(), user).orElseThrow(
				() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, transfer.getFromAccountId())));
		Account toAccount = accountRepository.findByAccountNo(transfer.getToAccountId())
				.orElseThrow(() -> new ResourceNotFoundException(
						String.format(ACCOUNT_NOT_FOUND_MSG, transfer.getToAccountId().getId())));
		if (transfer.getFromAccountId().equals(transfer.getToAccountId())) {
			throw new BadRequestException("Money transfers cannot be made with the same accounts!");
		}
		if (!transfer.getCurrencyCode().equals(fromAccount.getCurrencyCode())) {
			throw new BadRequestException("Currency does not match with your account!");
		}
		if (!toAccount.getCurrencyCode().equals(fromAccount.getCurrencyCode())) {
			throw new BadRequestException("Currency does not match!");
		}
		if (transfer.getTransactionAmount() > fromAccount.getBalance()) {
			throw new BadRequestException("not enough funds available for transfer");
		}
		if (fromAccount.getAccountStatusType().equals(AccountStatusType.CLOSED)
				|| fromAccount.getAccountStatusType().equals(AccountStatusType.SUSPENDED)
				|| toAccount.getAccountStatusType().equals(AccountStatusType.CLOSED)
				|| toAccount.getAccountStatusType().equals(AccountStatusType.SUSPENDED)) {
			throw new BadRequestException("You can transfer money between active accounts only!");
		}
		final Double newFromBalance = fromAccount.getBalance() - transfer.getTransactionAmount();
		final Double newToBalance = toAccount.getBalance() + transfer.getTransactionAmount();
		Date date = new Date();
		long time = date.getTime();
		Timestamp transactionDate = new Timestamp(time);
		Transfer transfer1 = new Transfer(fromAccount, transfer.getToAccountId().getId(), user,
				transfer.getTransactionAmount(), newFromBalance, transfer.getCurrencyCode(), transactionDate,
				transfer.getDescription());
		fromAccount.setBalance(newFromBalance);
		toAccount.setBalance(newToBalance);
		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);
		transferRepository.save(transfer1);
		return newFromBalance;
	}

// ---------------Get Transfers just for Own----------------------------------->>>

	public List<TransferUserDTO> findAllByID(Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		return transferRepository.findAllByUserId(user);
	}

	// ---------------Get Transfer By Transfer ID just for Own----------------->>>

	public Optional<TransferUserDTO> findById(Long id, Long userid) throws ResourceNotFoundException {
		User user = userRepository.findById(userid)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userid)));
		return transferRepository.findByIdAndUserId(id, user);
	}

	// ---------------Get Transfer By Account No just for Own----------------->>>

	public List<TransferUserDTO> findAllByAccountNo(Long accountNo, Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
		Account account = accountRepository.findByAccountNo(accountNumber)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
		return transferRepository.findAllByUserIdAndFromAccountId(user, account);
	}
	
	
	
	
//  ***********************************************************************

//							AUTH

//***********************************************************************
	
	
	
    //---------------Get All Transfers - AUTH----------------->>>   
	
    public List<TransferAdminDTO> fetchAllTransfers(Long id){
    	User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
		Set<String> userRoles = user.getRoles();
        if (userRoles.contains("Manager")||userRoles.contains("Employee"))
            return transferRepository.findAllBy();
        else
            return transferRepository.findAllByRole(UserRole.ROLE_CUSTOMER);
    }
    
    //---------------Get  Transfers By User ID - AUTH----------------->>>   
    
    public List<TransferAdminDTO> findByUserId(Long id, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        Set<String> adminRoles = admin.getRoles();
    	Set<String> userRoles = user.getRoles();
        if (adminRoles.contains("Manager") || userRoles.contains("Customer"))
            return transferRepository.findAllByUserIdOrderById(user);
        else
            throw new BadRequestException(String.format("You dont have permission to access transfer " +
                    "with userId %d", userId));
    }
    
    //---------------Get  Transfers By Account No - AUTH----------------->>>  
    
    public List<TransferAdminDTO> findAllByAccountNoAuth(Long accountNo, Long id) throws ResourceNotFoundException {
    	 User admin = userRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
        Account account = accountRepository.findByAccountNo(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, accountNo)));
        User user = userRepository.findById(account.getUserId().getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        Set<String> adminRoles = admin.getRoles();
    	Set<String> userRoles = user.getRoles();
        if (adminRoles.contains("Manager") || userRoles.contains("Customer"))
             return transferRepository.findAllByFromAccountIdOrderById(account);
        else
            throw new BadRequestException(String.format("You dont have permission to access transfer " +
                    "with accountNo %d", accountNo));
    }
    
    
    //---------------Get  Transfers By id - AUTH----------------->>>   
    
    public TransferAdminDTO findByIdAuth(Long userid, Long id) throws ResourceNotFoundException {
        User admin = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
        TransferAdminDTO transfer = transferRepository.findByIdOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TRANSFER_NOT_FOUND_MSG, id)));
        User user = userRepository.findById(transfer.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,
                        transfer.getUserId())));
        Set<String> adminRoles = admin.getRoles();
    	Set<String> userRoles = user.getRoles();
        if (adminRoles.contains("Manager") || userRoles.contains("Customer"))
              return transfer;
        else
            throw new BadRequestException(String.format("You dont have permission to access transfer with id %d", id));
    }
    
}
