package com.oop.library_management.common;

import java.util.List;

public record PageResponse<T>(

		List<T> content,
		Integer number,
		Integer size,
		Long totalElements,
		Integer totalPages,
		Boolean first,
		Boolean last
) {
}
