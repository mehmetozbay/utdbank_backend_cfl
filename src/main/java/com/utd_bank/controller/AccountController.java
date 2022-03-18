package com.utd_bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utd_bank.domain.Account;
import com.utd_bank.security.service.dto.AccountDTO;
import com.utd_bank.service.AccountService;

import lombok.AllArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController

@RequestMapping("/account")
public class AccountController {
//	--------------------CREATE AN ACCOUNT For Employee and Manager------------------------>>>>

	public AccountService accountService;

	@PostMapping("/create")
	@PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Map<String, Object>> createAccount(HttpServletRequest request,
			@Valid @RequestBody Account account) {
		Long id = (Long) request.getAttribute("id");
		Long accId = accountService.createAccountJustOwn(id, account);
		Map<String, Object> map = new HashMap<>();
		map.put("Account created successfully!", true);
		map.put("AccountId", accId);
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}
//	--------------------Get Accounts Just for Own------------------------>>>>
	   @GetMapping("")
	    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
	    public ResponseEntity<List<AccountDTO>> getAccountsBySsn(HttpServletRequest request){
	        Long id = (Long) request.getAttribute("id");
	        List<AccountDTO> account = accountService.findAllByID(id);
	        return new ResponseEntity<>(account, HttpStatus.OK);
	    }
	
	
//	--------------------Get Account By Account No Just for Own------------------------>>>>
	
    @GetMapping("/{accountNo}/user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Account> getAccountByIDAccountNo(@PathVariable Long accountNo,
                                                        HttpServletRequest request){
    	Long id = (Long) request.getAttribute("id");
        Account account = accountService.findByIDAccountNo(accountNo, id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
    
//	--------------------Delete Account By Account No Just for Own (Employee Manager)------------->>>>
    
    @DeleteMapping("/{accountNo}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> deleteAccount(HttpServletRequest request, @PathVariable Long accountNo){
        Long id = (Long) request.getAttribute("id");
        accountService.removeByAccountId(id, accountNo);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
	
//	--------------------Update Account By Account No Just for Own (Employee Manager)------------->>>>
    
    @PutMapping("/{accountNo}/update")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateAccount(HttpServletRequest request,
                                                              @PathVariable Long accountNo,
                                                              @Valid @RequestBody Account account) {
        Long id = (Long) request.getAttribute("id");
        accountService.updateAccount(id, accountNo, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
