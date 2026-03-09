package com.oop.library_management.exception;

public class InsufficientAmount extends RuntimeException {
	public InsufficientAmount(String message) {
		super(message);
	}
}
