package com.oop.library_management.author;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {

	List<Author> findByFullNameContainingIgnoreCase(String fullName);

	Page<Author> findAllByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

	Optional<Author> findByFullNameIgnoreCase(String fullName);

	boolean existsByFullNameIgnoreCase(String fullName);
}
