package com.oop.library_management.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.oop.library_management.dto.exception.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ErrorResponseDTO createErrorResponse(HttpStatus status, String message) {

		return new ErrorResponseDTO(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message
		);
	}

	private ErrorResponseDTO createErrorResponse(HttpStatus status, String message, Map<String, String> validationErrors) {

		return new ErrorResponseDTO(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				validationErrors
		);
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponseDTO> handleOptimisticLockingFailure(
			ObjectOptimisticLockingFailureException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.CONFLICT,
				"Conflict detected: The resource was modified by another transaction. Please refresh and try again."
		);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(InsufficientAmount.class)
	public ResponseEntity<ErrorResponseDTO> handleInsufficientTotalCopiesException(
			InsufficientAmount ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.CONFLICT,
				ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
			IllegalArgumentException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.BAD_REQUEST,
				ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(InvalidUserDataException.class)
	public ResponseEntity<ErrorResponseDTO> handleValidationException(
			InvalidUserDataException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.BAD_REQUEST,
				ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex
	) {

		Map<String, String> validationErrors = new HashMap<>();

		// Handle field errors
		ex.getBindingResult().getFieldErrors().forEach(error ->
				validationErrors.put(error.getField(), error.getDefaultMessage())
		);

		// Handle global/class-level errors
		ex.getBindingResult().getGlobalErrors().forEach(error ->
				validationErrors.put(error.getObjectName(), error.getDefaultMessage())
		);

		ErrorResponseDTO errorResponse = createErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Validation failed for one or more fields",
				validationErrors
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(com.oop.library_management.exception.AuthenticationException.class)
	public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
			com.oop.library_management.exception.AuthenticationException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.UNAUTHORIZED,
				ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
	public ResponseEntity<ErrorResponseDTO> handleSpringAuthenticationException(
			org.springframework.security.core.AuthenticationException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.UNAUTHORIZED,
				"Authentication failed: " + ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
			ResourceNotFoundException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.NOT_FOUND,
				ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyException(
			ResourceAlreadyExistsException ex
	) {

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.CONFLICT,
				ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
			jakarta.validation.ConstraintViolationException ex
	) {

		Map<String, String> errors = new HashMap<>();

		ex.getConstraintViolations().forEach(violation -> {
			String propertyPath = violation.getPropertyPath().toString();
			String message = violation.getMessage();
			errors.put(propertyPath.isEmpty() ? "validation" : propertyPath, message);
		});

		ErrorResponseDTO errorResponse = createErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Validation failed for one or more fields",
				errors
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidationException(
			HandlerMethodValidationException ex
	) {

		Map<String, String> errors = new HashMap<>();

		ex.getAllErrors().forEach(error -> {
					String fieldName = "validation";

					if (error instanceof org.springframework.validation.FieldError fieldError) {
						fieldName = fieldError.getField();
					} else if (error.getCodes() != null && error.getCodes().length > 0) {
						String code = error.getCodes()[0];
						String[] parts = code.split("\\.");
						if (parts.length > 0) {
							fieldName = parts[parts.length - 1];
						}
					}

					errors.put(fieldName, error.getDefaultMessage());
				}
		);

		ErrorResponseDTO errorResponse = createErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Validation failed",
				errors
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex
	) {

		// Optional: Extract a user-friendly message from the root cause
		String message = "Invalid payload";

		// You can inspect ex.getCause() for specific Jackson errors if needed
		if (ex.getCause() instanceof JsonParseException) {
			message = "Invalid JSON syntax: " + ex.getCause().getMessage();
		}

		ErrorResponseDTO errorResponse = createErrorResponse(
				HttpStatus.BAD_REQUEST,
				message
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
		// Log the full stack trace
		System.err.println("--- UNHANDLED EXCEPTION ---");
		System.err.println("Exception type: " + ex.getClass().getName());
		System.err.println("Message: " + ex.getMessage());
		ex.printStackTrace();

		ErrorResponseDTO error = createErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred: " + ex.getMessage()
		);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
