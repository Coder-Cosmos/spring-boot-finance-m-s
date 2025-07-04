package com.financemanager.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financemanager.dto.SavingsGoalDto;
import com.financemanager.entity.SavingsGoal;
import com.financemanager.entity.User;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.repository.SavingsGoalRepository;
import com.financemanager.repository.TransactionRepository;


@Service
@Transactional
public class SavingsGoalService {
    
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
 
    public SavingsGoalDto createSavingsGoal(SavingsGoalDto goalDto, User user) {
        SavingsGoal goal = new SavingsGoal();
        goal.setGoalName(goalDto.getGoalName());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setTargetDate(goalDto.getTargetDate());
        goal.setUser(user);
        
        // Use startDate from DTO if provided, otherwise it will default to current date in entity
        if (goalDto.getStartDate() != null) {
            goal.setStartDate(goalDto.getStartDate());
        }
        
        // Validate that start date is not after target date
        if (goal.getStartDate() != null && goal.getTargetDate() != null && 
            goal.getStartDate().isAfter(goal.getTargetDate())) {
            throw new IllegalArgumentException("Start date cannot be after target date");
        }
        
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        updateGoalProgress(savedGoal);
        
        return convertToDto(savedGoal);
    }
    
 
    public List<SavingsGoalDto> getAllSavingsGoals(User user) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUserOrderByCreatedAtDesc(user);
        return goals.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public SavingsGoalDto getSavingsGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user);
        if (goal == null) {
            throw new ResourceNotFoundException("Savings goal not found");
        }
        return convertToDto(goal);
    }
    
 
    public SavingsGoalDto updateSavingsGoal(Long id, SavingsGoalDto goalDto, User user) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user);
        if (goal == null) {
            throw new ResourceNotFoundException("Savings goal not found");
        }
        
        if (goalDto.getTargetAmount() != null) {
            goal.setTargetAmount(goalDto.getTargetAmount());
        }
        if (goalDto.getTargetDate() != null) {
            goal.setTargetDate(goalDto.getTargetDate());
        }
        
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        updateGoalProgress(savedGoal);
        
        return convertToDto(savedGoal);
    }

    public void deleteSavingsGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUser(id, user);
        if (goal == null) {
            throw new ResourceNotFoundException("Savings goal not found");
        }
        
        savingsGoalRepository.delete(goal);
    }
    
    
    public void updateGoalsProgress(User user) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUserOrderByCreatedAtDesc(user);
        goals.forEach(this::updateGoalProgress);
    }
   
    private void updateGoalProgress(SavingsGoal goal) {
        BigDecimal totalIncome = transactionRepository.calculateTotalIncomeSince(goal.getUser(), goal.getStartDate());
        BigDecimal totalExpenses = transactionRepository.calculateTotalExpensesSince(goal.getUser(), goal.getStartDate());
        BigDecimal netSavings = totalIncome.subtract(totalExpenses);
        
        BigDecimal currentProgress = netSavings.max(BigDecimal.ZERO).min(goal.getTargetAmount());
        goal.setCurrentProgress(currentProgress);
        
        savingsGoalRepository.save(goal);
    }
    

    private SavingsGoalDto convertToDto(SavingsGoal goal) {
        SavingsGoalDto dto = new SavingsGoalDto();
        dto.setId(goal.getId());
        dto.setGoalName(goal.getGoalName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setTargetDate(goal.getTargetDate());
        dto.setStartDate(goal.getStartDate());
        dto.setCurrentProgress(goal.getCurrentProgress());
        dto.setProgressPercentage(goal.getProgressPercentage());
        dto.setRemainingAmount(goal.getRemainingAmount());
        return dto;
    }
}
