package com.oop.library_management.dto.loan;

import java.util.List;
import jakarta.validation.constraints.*;


public record ReturnRequestDTO(
        @NotEmpty(message = "Member number is required")
        String membershipNumber,

        @NotEmpty(message = "At least one book ID and amount is required")
        List<BookAmount> bookAmounts
) {
    
}
