package com.oop.library_management.author;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AuthorSpecifications {
	private AuthorSpecifications() {
	}

	public static Specification<Author> byCriteria(AuthorSearchCriteria c) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (c != null && c.name() != null && !c.name().trim().isEmpty()) {
				predicates.add(
					cb.like(cb.lower(root.get("fullName")), "%" + c.name().trim().toLowerCase() + "%")
				);
			}

			if (c != null && c.type() != null) {
				predicates.add(cb.equal(root.get("type"), c.type()));
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
