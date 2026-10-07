package com.sanskruti.fintrack.service;

import com.sanskruti.fintrack.dto.ExpenseRequest;
import com.sanskruti.fintrack.dto.ExpenseResponse;
import com.sanskruti.fintrack.model.Expense;
import com.sanskruti.fintrack.model.User;
import com.sanskruti.fintrack.repository.ExpenseRepository;
import com.sanskruti.fintrack.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenses;
    private final UserRepository users;

    public ExpenseService(ExpenseRepository expenses, UserRepository users) {
        this.expenses = expenses;
        this.users = users;
    }

    public ExpenseResponse create(String username, ExpenseRequest r) {
        User owner = user(username);
        Expense saved = expenses.save(new Expense(r.amount(), r.category(), r.description(), r.date(), owner));
        return ExpenseResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> list(String username, String category, Pageable pageable) {
        User owner = user(username);
        Page<Expense> page = (category == null || category.isBlank())
                ? expenses.findByOwner(owner, pageable)
                : expenses.findByOwnerAndCategoryIgnoreCase(owner, category, pageable);
        return page.map(ExpenseResponse::from);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse get(String username, Long id) {
        return ExpenseResponse.from(owned(username, id));
    }

    public ExpenseResponse update(String username, Long id, ExpenseRequest r) {
        Expense e = owned(username, id);
        e.update(r.amount(), r.category(), r.description(), r.date());
        return ExpenseResponse.from(e);
    }

    public void delete(String username, Long id) {
        expenses.delete(owned(username, id));
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> monthlySummary(String username, YearMonth month) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : expenses.sumByCategory(user(username), month.atDay(1), month.atEndOfMonth())) {
            result.put((String) row[0], (BigDecimal) row[1]);
        }
        return result;
    }

    private User user(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
    }

    private Expense owned(String username, Long id) {
        return expenses.findByIdAndOwner(id, user(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
    }
}
