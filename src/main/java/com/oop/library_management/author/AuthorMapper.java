package com.oop.library_management.author;

import com.oop.library_management.common.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper extends BaseMapper<Author, AuthorResponseDTO> {

	@Override
	public AuthorResponseDTO toDTO(Author author) {
		if (author == null) {
			return null;
		}

		return new AuthorResponseDTO(
			author.getId(),
			author.getFullName(),
			author.getFirstName(),
			author.getLastName(),
			author.getType()
		);
	}
}
