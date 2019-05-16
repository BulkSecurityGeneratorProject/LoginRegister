package com.aew.ManagmentAccount.repository;

import java.util.Optional;

import com.aew.ManagmentAccount.domain.Authority;
import com.aew.ManagmentAccount.domain.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(RoleName roleName);
}