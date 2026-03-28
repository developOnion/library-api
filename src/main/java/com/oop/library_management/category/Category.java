package com.oop.library_management.category;

import com.oop.library_management.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

	@Column(nullable = false, unique = true, length = 100)
	@NotBlank(message = "Category name is required")
	@Size(max = 100, message = "Category name must be at most 100 characters")
	private String name;

	public Category() {
		// Required by JPA
	}

	public Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

