package com.oop.library_management.service;

import com.oop.library_management.dto.category.CategoryRequestDTO;
import com.oop.library_management.dto.category.CategoryResponseDTO;
import com.oop.library_management.dto.search_criteria.CategorySearchCriteria;
import com.oop.library_management.exception.ResourceAlreadyExistsException;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.mapper.CategoryMapper;
import com.oop.library_management.model.category.Category;
import com.oop.library_management.model.common.PageResponse;
import com.oop.library_management.repository.CategoryRepository;
import com.oop.library_management.repository.specification.CategorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService implements CrudService<CategoryRequestDTO, CategoryResponseDTO>, SearchableService<CategorySearchCriteria, CategoryResponseDTO> {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public CategoryService(
		CategoryRepository categoryRepository,
		CategoryMapper categoryMapper
	) {
		this.categoryRepository = categoryRepository;
		this.categoryMapper = categoryMapper;
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<CategoryResponseDTO> search(CategorySearchCriteria criteria, int page, int size) {

		if (criteria == null || (criteria.name() == null || criteria.name().trim().isEmpty())) {
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
			Sort.by("createdAt").descending()
				.and(Sort.by("name").ascending())
		);

		Page<Category> categories = categoryRepository.findAll(CategorySpecification.byCriteria(criteria), pageable);
		List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
			.map(categoryMapper::toDTO)
			.toList();

		return new PageResponse<>(
			categoryResponseDTOS,
			categories.getNumber(),
			categories.getSize(),
			categories.getTotalElements(),
			categories.getTotalPages(),
			categories.isFirst(),
			categories.isLast()
		);
	}

	@Override
	@Transactional
	public CategoryResponseDTO create(
		CategoryRequestDTO categoryRequestDTO
	) {

		if (
			categoryRepository
				.existsByNameIgnoreCase(categoryRequestDTO.name())
		) {

			throw new ResourceAlreadyExistsException("Category already exists");
		}

		Category category = new Category(categoryRequestDTO.name());
		Category savedCategory = categoryRepository.save(category);

		return categoryMapper.toDTO(savedCategory);
	}

	@Override
	@Transactional(readOnly = true)
	public CategoryResponseDTO getById(Long id) {

		return categoryRepository.findById(id)
			.map(categoryMapper::toDTO)
			.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
	}

	/**
	 * Not supported — categories are immutable reference data.
	 */
	@Override
	public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
		throw new UnsupportedOperationException("Category update is not supported");
	}

	/**
	 * Not supported — categories are immutable reference data.
	 */
	@Override
	public void delete(Long id) {
		throw new UnsupportedOperationException("Category deletion is not supported");
	}
}
