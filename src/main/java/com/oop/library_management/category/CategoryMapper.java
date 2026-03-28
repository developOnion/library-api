package com.oop.library_management.category;

import com.oop.library_management.common.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper extends BaseMapper<Category, CategoryResponseDTO> {

	@Override
	public CategoryResponseDTO toDTO(Category category) {

		if (category == null) {
			return null;
		}

		return new CategoryResponseDTO(
			category.getId(),
			category.getName()
		);
	}
}
