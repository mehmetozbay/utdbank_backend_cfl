package com.utd_bank.repository;



import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utd_bank.domain.Role;
import com.utd_bank.domain.enumeration.UserRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(UserRole name);
}
