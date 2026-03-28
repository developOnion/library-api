package com.oop.library_management.validation.atleastonename;

import com.oop.library_management.author.AuthorRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneNameValidator implements ConstraintValidator<AtLeastOneName, AuthorRequestDTO> {

	@Override
	public boolean isValid(AuthorRequestDTO authorDTO, ConstraintValidatorContext context) {

		if (authorDTO == null) {
			return true; // Let @NotNull handle null case if needed
		}

		return (authorDTO.firstName() != null && !authorDTO.firstName().isBlank()) ||
			(authorDTO.lastName() != null && !authorDTO.lastName().isBlank());
	}
}
