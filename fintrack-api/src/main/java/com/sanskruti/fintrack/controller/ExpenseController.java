package com.sanskruti.fintrack.controller;

import com.sanskruti.fintrack.dto.ExpenseRequest;
import com.sanskruti.fintrack.dto.ExpenseResponse;
import com.sanskruti.fintrack.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(Authentication auth, @Valid @RequestBody ExpenseRequest req) {
        return service.create(auth.getName(), req);
    }

    @GetMapping
    public Page<ExpenseResponse> list(Authentication auth,
                                      @RequestParam(required = false) String category,
                                      @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.list(auth.getName(), category, pageable);
    }

    @GetMapping("/{id}")
    public ExpenseResponse get(Authentication auth, @PathVariable Long id) {
        return service.get(auth.getName(), id);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody ExpenseRequest req) {
        return service.update(auth.getName(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable Long id) {
        service.delete(auth.getName(), id);
    }

    /** GET /api/expenses/summary?month=2026-10 -> total spend per category */
    @GetMapping("/summary")
    public Map<String, BigDecimal> summary(Authentication auth,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return service.monthlySummary(auth.getName(), month);
    }
}
