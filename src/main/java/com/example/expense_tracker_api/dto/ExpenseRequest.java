package com.example.expense_tracker_api.dto;

import com.example.expense_tracker_api.model.Category;

import java.time.LocalDate;

public record ExpenseRequest(String description , Double amount , Category category , LocalDate date) {

}
