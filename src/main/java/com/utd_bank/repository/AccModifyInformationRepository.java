package com.utd_bank.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utd_bank.domain.AccountModifyInformation;

@Repository
public interface AccModifyInformationRepository extends JpaRepository<AccountModifyInformation, Long> {
}
