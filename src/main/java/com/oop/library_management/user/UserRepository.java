package com.oop.library_management.user;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select u from User u where u.username = :username")
	Optional<User> findByUsernameWithLock(String username);
	
	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);
}
