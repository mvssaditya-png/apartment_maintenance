package com.apartment.maintenance.repository;

import com.apartment.maintenance.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository
        extends JpaRepository<Expense, UUID> {

    List<Expense> findBySiteIdOrderByExpenseDateDesc(UUID siteId);

    @Query("""
    SELECT COALESCE(SUM(e.amount),0)
    FROM Expense e
    WHERE e.siteId = :siteId
      AND EXTRACT(MONTH FROM e.expenseDate) = :month
      AND EXTRACT(YEAR FROM e.expenseDate) = :year
""")
    Double getMonthlyExpenses(
            UUID siteId,
            String month,
            String year);

    @Query("""
    SELECT COALESCE(SUM(e.amount),0)
    FROM Expense e
    WHERE e.siteId = :siteId
      AND (
           EXTRACT(YEAR FROM e.expenseDate) < :year
           OR (
                EXTRACT(YEAR FROM e.expenseDate) = :year
                AND EXTRACT(MONTH FROM e.expenseDate) < :month
           )
      )
""")
    Double getExpensesBefore(
            UUID siteId,
            String month,
            String year);
}