package com.oop.library_management.dto.search_criteria;

import com.oop.library_management.model.author.AuthorType;

public record AuthorSearchCriteria(

	String name,
	AuthorType type
) {
}
