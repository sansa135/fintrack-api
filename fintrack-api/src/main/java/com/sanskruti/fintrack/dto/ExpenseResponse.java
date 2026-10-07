package com.sanskruti.fintrack.dto;

import com.sanskruti.fintrack.model.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, BigDecimal amount, String category, String description, LocalDate date) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getAmount(), e.getCategory(), e.getDescription(), e.getDate());
    }
}
