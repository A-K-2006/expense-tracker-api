package com.example.expense_tracker_api.service;

import com.example.expense_tracker_api.dto.ExpenseRequest;
import com.example.expense_tracker_api.dto.ExpenseResponse;
import com.example.expense_tracker_api.model.Expense;
import com.example.expense_tracker_api.model.User;
import com.example.expense_tracker_api.respository.ExpenseRepository;
import com.example.expense_tracker_api.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository ;
    private final UserRepository userRepository ;

    public ExpenseResponse addExpense(String username , ExpenseRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow() ;
        Expense expense = new Expense() ;
        expense.setDescription(request.description());
        expense.setAmount(request.amount()) ;
        expense.setCategory(request.category());
        expense.setDate(request.date() != null ? request.date() : LocalDate.now()) ;
        expense.setUser(user);
        return ExpenseResponse.fromEntity(expenseRepository.save(expense)) ;
    }

    public List<ExpenseResponse> getExpenses(String username , String filter , LocalDate startDate , LocalDate endDate) {
        User user = userRepository.findByUsername(username).orElseThrow() ;
        LocalDate now = LocalDate.now() ;
        LocalDate start = now ;
        LocalDate end = now ;

        switch (filter) {
            case "week" -> start = now.minusWeeks(1) ;
            case "month" -> start = now.minusMonths(1) ;
            case "3month" -> start = now.minusMonths(3) ;
            case "custom" -> {
                if(startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and End dates are required for custom filter") ;
                }
                start = startDate ;
                end = endDate ;
            }
            case "all" -> {
                return expenseRepository.findByUserId(user.getId()).stream().map(ExpenseResponse::fromEntity).toList() ;
            }
            default -> throw new IllegalArgumentException("Invalid filter. User : week , month , 3month , custom or all") ;
        }
        return expenseRepository.findByUserIdAndDateBetween(user.getId(), start, end)
                .stream()
                .map(ExpenseResponse::fromEntity)
                .toList();
    }

    public ExpenseResponse updateExpense(String username , Long expenseId , ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if(!expense.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized") ;
        }
        expense.setDescription(request.description());
        expense.setAmount(request.amount()) ;
        expense.setCategory(request.category());
        expense.setDate(request.date() != null ? request.date() : expense.getDate()) ;
        return ExpenseResponse.fromEntity(expenseRepository.save(expense)) ;
    }

    public void deleteExpense(String username , Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found")) ;
        if(!expense.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized") ;
        }
        expenseRepository.delete(expense) ;
    }
}
