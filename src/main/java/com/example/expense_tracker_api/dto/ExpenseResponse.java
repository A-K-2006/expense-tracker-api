package com.example.expense_tracker_api.dto;

import com.example.expense_tracker_api.model.Category;

import java.time.LocalDate;

public record ExpenseResponse(Long id , String description , Double amount , Category category , LocalDate date) {
    public static ExpenseResponse fromEntity(com.example.expense_tracker_api.model.Expense expense) {
        return new ExpenseResponse(expense.getId() , expense.getDescription() , expense.getAmount() , expense.getCategory() , expense.getDate()) ;
    }
}
