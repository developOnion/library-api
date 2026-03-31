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
	private final LoanQueryService loanQueryService;

	public LoanController(LoanService loanService, LoanQueryService loanQueryService) {
		this.loanService = loanService;
		this.loanQueryService = loanQueryService;
	}

	@PostMapping("/borrow")
	@PreAuthorize("hasAuthority('LIBRARIAN')")
	public ResponseEntity<BorrowResponseDTO> borrowBook(
		@Valid @RequestBody BorrowRequestDTO borrowRequestDTO
	) {
		BorrowResponseDTO response = loanService.borrowBook(borrowRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/return")
	@PreAuthorize("hasAuthority('LIBRARIAN')")
	public ResponseEntity<BorrowResponseDTO> returnBook(
		@Valid @RequestBody ReturnRequestDTO returnRequestDTO
	) {
		BorrowResponseDTO response = loanService.returnBook(returnRequestDTO);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/history")
	@PreAuthorize("hasAuthority('LIBRARIAN')")
	public ResponseEntity<PageResponse<LoanHistoryResponseDTO>> getAllLoanHistory(
		@RequestParam(required = false) String status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {

		PageResponse<LoanHistoryResponseDTO> response;
		if (status != null && !status.trim().isEmpty()) {
			response = loanQueryService.getLoanHistory(status, page, size);
		} else {
			response = loanQueryService.getLoanHistory(page, size);
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/history/{userId}")
	@PreAuthorize("hasAuthority('LIBRARIAN') or (hasAuthority('MEMBER') and #userId == authentication.principal.id)")
	public ResponseEntity<PageResponse<LoanHistoryResponseDTO>> getAllLoanHistoryByUserId(
		@PathVariable Long userId,
		@RequestParam(required = false) String status,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		PageResponse<LoanHistoryResponseDTO> response;
		if (status != null && !status.trim().isEmpty()) {
			response = loanQueryService.getLoanHistoryByUserId(userId, status, page, size);
		} else {
			response = loanQueryService.getLoanHistoryByUserId(userId, page, size);
		}
		return ResponseEntity.ok().body(response);
	}


}
