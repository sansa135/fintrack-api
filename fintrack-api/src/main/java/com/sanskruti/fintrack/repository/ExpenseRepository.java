package com.sanskruti.fintrack.repository;

import com.sanskruti.fintrack.model.Expense;
import com.sanskruti.fintrack.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByOwner(User owner, Pageable pageable);

    Page<Expense> findByOwnerAndCategoryIgnoreCase(User owner, String category, Pageable pageable);

    Optional<Expense> findByIdAndOwner(Long id, User owner);

    @Query("select e.category, sum(e.amount) from Expense e " +
           "where e.owner = :owner and e.date between :from and :to group by e.category")
    List<Object[]> sumByCategory(@Param("owner") User owner,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to);
}
