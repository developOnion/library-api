package com.oop.library_management.service;

import com.oop.library_management.dto.author.AuthorRequestDTO;
import com.oop.library_management.dto.author.AuthorResponseDTO;
import com.oop.library_management.dto.search_criteria.AuthorSearchCriteria;
import com.oop.library_management.exception.ResourceAlreadyExistsException;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.mapper.AuthorMapper;
import com.oop.library_management.model.author.Author;
import com.oop.library_management.model.common.PageResponse;
import com.oop.library_management.repository.AuthorRepository;
import com.oop.library_management.repository.specification.AuthorSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService implements CrudService<AuthorRequestDTO, AuthorResponseDTO>, SearchableService<AuthorSearchCriteria, AuthorResponseDTO> {

	private final AuthorRepository authorRepository;
	private final AuthorMapper authorMapper;

	public AuthorService(
		AuthorRepository authorRepository,
		AuthorMapper authorMapper
	) {
		this.authorRepository = authorRepository;
		this.authorMapper = authorMapper;
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<AuthorResponseDTO> search(AuthorSearchCriteria criteria, int page, int size) {

		if (criteria == null || (criteria.name() == null || criteria.name().trim().isEmpty()) && criteria.type() == null) {
			System.out.println("yes");
			return new PageResponse<>(
				List.of(),
				page,
				size,
				0L,
				0,
				true,
				true
			);
		}

		Pageable pageable = PageRequest.of(
			page,
			size,
			Sort.by("lastName").ascending()
				.and(Sort.by("firstName").ascending())
		);

		Page<Author> authors = authorRepository.findAll(AuthorSpecifications.byCriteria(criteria), pageable);
		List<AuthorResponseDTO> authorResponseDTOS = authors.stream()
			.map(authorMapper::toDTO)
			.toList();

		return new PageResponse<>(
			authorResponseDTOS,
			authors.getNumber(),
			authors.getSize(),
			authors.getTotalElements(),
			authors.getTotalPages(),
			authors.isFirst(),
			authors.isLast()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorResponseDTO getById(final Long id) {

		return authorRepository.findById(id)
			.map(authorMapper::toDTO)
			.orElseThrow(() -> new ResourceNotFoundException("Author not found"));
	}

	@Override
	@Transactional
	public AuthorResponseDTO create(
		final AuthorRequestDTO authorRequestDTO
	) {

		if (
			authorRepository
				.existsByFullNameIgnoreCase(
					authorRequestDTO.firstName() + " " + authorRequestDTO.lastName()
				)
		) {

			throw new ResourceAlreadyExistsException("Author already exists");
		}

		Author author = new Author(
			authorRequestDTO.firstName(),
			authorRequestDTO.lastName(),
			authorRequestDTO.type()
		);

		Author savedAuthor = authorRepository.save(author);

		return authorMapper.toDTO(savedAuthor);
	}

	/**
	 * Not supported — authors are immutable reference data.
	 */
	@Override
	public AuthorResponseDTO update(Long id, AuthorRequestDTO request) {
		throw new UnsupportedOperationException("Author update is not supported");
	}

	/**
	 * Not supported — authors are immutable reference data.
	 */
	@Override
	public void delete(Long id) {
		throw new UnsupportedOperationException("Author deletion is not supported");
	}
}
