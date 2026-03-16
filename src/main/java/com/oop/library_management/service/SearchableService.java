package com.oop.library_management.service;

import com.oop.library_management.model.common.PageResponse;

public interface SearchableService<C, R> {
	PageResponse<R> search(C criteria, int page, int size);
}