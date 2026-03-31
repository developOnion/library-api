package com.oop.library_management.loan;

import com.oop.library_management.common.PageResponse;
import com.oop.library_management.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanQueryService {

	private final LoanRepository loanRepository;
	private final LoanMapper loanMapper;

	public LoanQueryService(
		LoanRepository loanRepository,
		LoanMapper loanMapper
	) {
		this.loanRepository = loanRepository;
		this.loanMapper = loanMapper;
	}

	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> findLoanById(Long userId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("loanDate").descending());
		Page<Loan> loans = loanRepository.findByMember_Id(userId, pageable);

		return loanMapper.buildPageResponse(loans);
	}

	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistoryByUserId(Long userId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findByMember_Id(userId, pageable);

		return loanMapper.buildPageResponse(loans);
	}

	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistoryByUserId(Long userId, String status, int page, int size) {

		LoanStatus loanStatus = parseLoanStatus(status);

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findByMember_IdAndStatus(userId, loanStatus, pageable);

		return loanMapper.buildPageResponse(loans);
	}

	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistory(String status, int page, int size) {

		LoanStatus loanStatus = parseLoanStatus(status);

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanStatus != null
			? loanRepository.findByStatus(loanStatus, pageable)
			: loanRepository.findAll(pageable);

		return loanMapper.buildPageResponse(loans);
	}

	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistory(int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findAll(pageable);

		return loanMapper.buildPageResponse(loans);
	}

	private LoanStatus parseLoanStatus(String status) {
		try {
			return LoanStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid loan status: " + status);
		}
	}
}
