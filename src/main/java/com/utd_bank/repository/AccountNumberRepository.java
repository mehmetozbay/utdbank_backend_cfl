package com.utd_bank.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.utd_bank.domain.AccountNumber;

@Transactional
@Repository
public interface AccountNumberRepository extends JpaRepository<AccountNumber, Long> {
}
