package com.oop.library_management.common;

public abstract class BaseMapper<E, D> {

	public abstract D toDTO(E entity);
}
