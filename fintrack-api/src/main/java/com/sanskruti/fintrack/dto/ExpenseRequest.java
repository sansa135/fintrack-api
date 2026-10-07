package com.sanskruti.fintrack.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotBlank @Size(max = 50) String category,
        @Size(max = 255) String description,
        @NotNull LocalDate date) {}
