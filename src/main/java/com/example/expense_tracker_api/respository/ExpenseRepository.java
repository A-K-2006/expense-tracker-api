package com.example.expense_tracker_api.respository;

import com.example.expense_tracker_api.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense , Long> {
    List<Expense> findByUserId(Long userId) ;
    List<Expense> findByUserIdAndDateBetween(Long userId , LocalDate startDate , LocalDate endDate) ;
}
