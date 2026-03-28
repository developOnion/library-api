package com.oop.library_management.common;

public interface CrudService<RQ, RS> {

	RS getById(Long id);

	RS create(RQ request);

	RS update(Long id, RQ request);

	void delete(Long id);
}
