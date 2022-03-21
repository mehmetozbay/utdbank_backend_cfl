package com.utd_bank.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utd_bank.domain.Account;
import com.utd_bank.domain.Transfer;
import com.utd_bank.domain.User;
import com.utd_bank.domain.enumeration.UserRole;
import com.utd_bank.exception.ResourceNotFoundException;
import com.utd_bank.security.service.dto.TransferAdminDTO;
import com.utd_bank.security.service.dto.TransferUserDTO;

@Transactional
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<TransferAdminDTO> findByIdOrderById(Long id) throws ResourceNotFoundException;

    Optional<TransferUserDTO> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    List<TransferAdminDTO> findAllBy() throws ResourceNotFoundException;

    List<TransferAdminDTO> findAllByUserIdOrderById(User id) throws ResourceNotFoundException;

//    @Query("SELECT t from Transfer t " +
//            "LEFT JOIN FETCH t.userId u " +
//            "LEFT JOIN FETCH u.roles r " +
//            "WHERE t.userId = ?1 and r.name = ?2")
//    List<TransferAdminDao> findAllByUserIdAndRole(User user, UserRole userRole) throws ResourceNotFoundException;

    @Query("SELECT t from Transfer t " +
            "LEFT JOIN FETCH t.userId u " +
            "LEFT JOIN FETCH u.roles r " +
            "WHERE r.name = ?1")
    List<TransferAdminDTO> findAllByRole(UserRole userRole);

    List<TransferUserDTO> findAllByUserId(User id) throws ResourceNotFoundException;

    List<TransferUserDTO> findAllByUserIdAndFromAccountId(User user, Account account);

    List<TransferAdminDTO> findAllByFromAccountIdOrderById(Account id) throws ResourceNotFoundException;

    List<Transfer> findAllByFromAccountId(Account id) throws ResourceNotFoundException;

//    void deleteAllByFromAccountId(Account id) throws ResourceNotFoundException;
}
