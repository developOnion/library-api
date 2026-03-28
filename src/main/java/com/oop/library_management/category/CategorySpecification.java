package com.oop.library_management.category;

import com.oop.library_management.category.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class CategorySpecification {
	private CategorySpecification() {
	}

	public static Specification<Category> byCriteria(CategorySearchCriteria c) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (c != null && c.name() != null && !c.name().trim().isEmpty()) {
				predicates.add(
					cb.like(cb.lower(root.get("name")), "%" + c.name().trim().toLowerCase() + "%")
				);
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
