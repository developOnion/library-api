package com.oop.library_management.loan;


import com.oop.library_management.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/loans")
@Tag(name = "Loan Management", description = "Endpoints for managing book loans and returns")
public class LoanController {

	private final LoanService loanService;

	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}

	@PostMapping("/borrow")
	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<BorrowResponseDTO> borrowBook(
		@Valid @RequestBody BorrowRequestDTO borrowRequestDTO
	) {
		BorrowResponseDTO response = loanService.borrowBook(borrowRequestDTO);
		return ResponseEntity.ok().body(response);
	}

	@PutMapping("/return")
	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<BorrowResponseDTO> returnBook(
		@Valid @RequestBody ReturnRequestDTO returnRequestDTO
	) {
		BorrowResponseDTO response = loanService.returnBook(returnRequestDTO);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/history")
	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PageResponse<LoanHistoryResponseDTO>> getAllLoanHistory(
		@RequestParam(required = false) String status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {

		PageResponse<LoanHistoryResponseDTO> response;
		if (status != null && !status.trim().isEmpty()) {
			response = loanService.getLoanHistory(status, page, size);
		} else {
			response = loanService.getLoanHistory(page, size);
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/history/{userId}")
	@PreAuthorize("hasAuthority('LIBRARIAN') or (hasAuthority('MEMBER') and #userId == authentication.principal.id)")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PageResponse<LoanHistoryResponseDTO>> getAllLoanHistoryByUserId(
		@PathVariable Long userId,
		@RequestParam(required = false) String status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		PageResponse<LoanHistoryResponseDTO> response;
		if (status != null && !status.trim().isEmpty()) {
			response = loanService.getLoanHistoryByUserId(userId, status, page, size);
		} else {
			response = loanService.getLoanHistoryByUserId(userId, page, size);
		}
		return ResponseEntity.ok().body(response);
	}


}
