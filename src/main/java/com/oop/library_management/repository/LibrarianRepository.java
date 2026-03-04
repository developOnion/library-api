package com.oop.library_management.repository;

import com.oop.library_management.model.user.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long> {
    
    Optional<Librarian> findById(Long id);

    Optional<Librarian> findByUsername(String username);
}
