package com.financemanager.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "savings_goals")
public class SavingsGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Goal name is required")
    @Column(nullable = false)
    private String goalName;
    
    @DecimalMin(value = "0.01", message = "Target amount must be positive")
    @NotNull(message = "Target amount is required")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;
    
    @Future(message = "Target date must be in the future")
    @NotNull(message = "Target date is required")
    @Column(nullable = false)
    private LocalDate targetDate;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentProgress = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal progressPercentage = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        remainingAmount = targetAmount;
    }
    
    @PreUpdate
    protected void onUpdate() {
        if (targetAmount != null && currentProgress != null) {
            remainingAmount = targetAmount.subtract(currentProgress);
            if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
                progressPercentage = currentProgress.divide(targetAmount, 1, RoundingMode.HALF_UP)
                                                  .multiply(new BigDecimal("100"));
            }
        }
    }
    
    public SavingsGoal() {}
    
    public SavingsGoal(String goalName, BigDecimal targetAmount, LocalDate targetDate, User user) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.user = user;
        this.startDate = LocalDate.now();
        this.remainingAmount = targetAmount;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGoalName() {
        return goalName;
    }
    
    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }
    
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
        if (currentProgress != null) {
            this.remainingAmount = targetAmount.subtract(currentProgress);
        }
    }
    
    public LocalDate getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public BigDecimal getCurrentProgress() {
        return currentProgress;
    }
    
    public void setCurrentProgress(BigDecimal currentProgress) {
        this.currentProgress = currentProgress;
        if (targetAmount != null) {
            this.remainingAmount = targetAmount.subtract(currentProgress);
            if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
                this.progressPercentage = currentProgress.divide(targetAmount, 1, RoundingMode.HALF_UP)
                                                        .multiply(new BigDecimal("100"));
            }
        }
    }
    
    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
    
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
