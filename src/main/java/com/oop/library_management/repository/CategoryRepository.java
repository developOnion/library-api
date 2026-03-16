package com.oop.library_management.repository;

import com.oop.library_management.model.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

	List<Category> findByNameContainingIgnoreCase(String name);

	boolean existsByNameIgnoreCase(String name);

	Page<Category> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
