package com.oop.library_management.loan;

@FunctionalInterface
public interface BorrowingPolicy {
	/**
	 * Determines if a borrowing request is allowed based on the requested amount
	 * and the number of books currently borrowed by the member.
	 *
	 * @param requestedAmount   The number of books the member is trying to borrow.
	 * @param currentlyBorrowed The number of books the member already has out.
	 * @return true if allowed, false otherwise.
	 */
	boolean isAllowed(int requestedAmount, int currentlyBorrowed);
}
