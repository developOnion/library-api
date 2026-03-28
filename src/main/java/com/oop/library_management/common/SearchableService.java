package com.oop.library_management.common;

import com.oop.library_management.common.PageResponse;

public interface SearchableService<C, R> {
	PageResponse<R> search(C criteria, int page, int size);
}