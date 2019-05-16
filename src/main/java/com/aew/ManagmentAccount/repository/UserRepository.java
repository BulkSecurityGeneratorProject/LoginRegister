package com.aew.ManagmentAccount.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.aew.ManagmentAccount.domain.User;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

    Page<User> findAllUsers(Pageable pageable);

}