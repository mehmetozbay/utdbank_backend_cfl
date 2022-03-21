package com.utd_bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utd_bank.security.service.dto.TransferAdminDTO;
import com.utd_bank.security.service.dto.TransferDTO;
import com.utd_bank.security.service.dto.TransferUserDTO;
import com.utd_bank.service.TransferService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController()
@RequestMapping("/transfer")
public class TransferController {

	private TransferService transferService;

//	***********************************************************************

//						JUST FOR OWN

//*************************************************************************

//---------------Create Transfer ---------------------------------------->>>

	@PostMapping("/create")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
	public ResponseEntity<Map<String, Object>> createTransfer(HttpServletRequest request,
			@Valid @RequestBody TransferDTO transfer) {
		Long id = (Long) request.getAttribute("id");
		Double currentBalance = transferService.create(id, transfer);

		Map<String, Object> map = new HashMap<>();
		map.put("Transfer created successfully!", true);
		map.put("currentBalance", currentBalance);
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}

// ---------------Get Transfers just for Own--------------------------------->>>

	@GetMapping("")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
	public ResponseEntity<List<TransferUserDTO>> getTransfersBySsn(HttpServletRequest request) {
		Long id = (Long) request.getAttribute("id");
		List<TransferUserDTO> transfers = transferService.findAllByID(id);
		return new ResponseEntity<>(transfers, HttpStatus.OK);
	}

//---------------Get Transfer By Transfer ID just for Own----------------->>>    
	
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Optional<TransferUserDTO>> getTransferBySsnId(@PathVariable Long id,
                                                        HttpServletRequest request){
        Long userid = (Long) request.getAttribute("id");
        Optional<TransferUserDTO> transfer = transferService.findById(id, userid);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }
    
  //---------------Get Transfer By Account No just for Own----------------->>>   
    
    @GetMapping("/{accountNo}/accountNo")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<TransferUserDTO>> getTransfersByAccountNo(HttpServletRequest request,
                                                                         @PathVariable Long accountNo){
    	Long id = (Long) request.getAttribute("id");
        List<TransferUserDTO> transfers = transferService.findAllByAccountNo(accountNo, id);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }
    
    
//  ***********************************************************************

//								AUTH

//***********************************************************************
    
    
    //---------------Get All Transfers - AUTH----------------->>>   
    
    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<TransferAdminDTO>> getAllTransfers(HttpServletRequest request){
        Long id = (Long) request.getAttribute("id");
        List<TransferAdminDTO> transfers = transferService.fetchAllTransfers(id);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    //---------------Get  Transfers By User ID - AUTH----------------->>>    
    
    @GetMapping("/user/{userId}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<TransferAdminDTO>> getTransfersByUserId(HttpServletRequest request,
                                                                       @PathVariable Long userId){
    	 Long id = (Long) request.getAttribute("id");
        List<TransferAdminDTO> transfer = transferService.findByUserId(id, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }
    
    //---------------Get  Transfers By Account No - AUTH----------------->>>    
    
    @GetMapping("/{accountNo}/accountNo/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<TransferAdminDTO>> getTransfersByAccountNoAuth(HttpServletRequest request,
                                                                         @PathVariable Long accountNo){
   	 Long id = (Long) request.getAttribute("id");
        List<TransferAdminDTO> transfers = transferService.findAllByAccountNoAuth(accountNo, id);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }
    
    //---------------Get  Transfers By id - AUTH----------------->>>   
    
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<TransferAdminDTO> getTransferById(HttpServletRequest request,
                                                            @PathVariable Long id){
    	 Long userid = (Long) request.getAttribute("id");
        TransferAdminDTO transfer = transferService.findByIdAuth(userid, id);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }
    
    

}
