package com.financemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financemanager.dto.MonthlyReportDto;
import com.financemanager.dto.YearlyReportDto;
import com.financemanager.entity.User;
import com.financemanager.service.ReportService;
import com.financemanager.service.UserService;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private UserService userService;
 
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportDto> getMonthlyReport(@PathVariable int year, 
                                                            @PathVariable int month,
                                                            HttpSession session) {
        try {
            User user = getCurrentUser(session);
            MonthlyReportDto report = reportService.generateMonthlyReport(user, month, year);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportDto> getYearlyReport(@PathVariable int year, HttpSession session) {
        try {
            User user = getCurrentUser(session);
            YearlyReportDto report = reportService.generateYearlyReport(user, year);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not authenticated: session is missing or expired");
        }
        return userService.findById(userId);
    }
}
