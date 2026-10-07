package com.sanskruti.fintrack.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses", indexes = @Index(name = "idx_expense_owner_date", columnList = "owner_id, date"))
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String category;

    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    protected Expense() {}

    public Expense(BigDecimal amount, String category, String description, LocalDate date, User owner) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public User getOwner() { return owner; }

    public void update(BigDecimal amount, String category, String description, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }
}
