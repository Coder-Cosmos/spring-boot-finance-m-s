package com.financemanager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financemanager.dto.CategoryDto;
import com.financemanager.entity.User;
import com.financemanager.service.CategoryService;
import com.financemanager.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(HttpSession session) {
        try {
            User user = getCurrentUser(session);
            List<CategoryDto> categories = categoryService.getCategoriesForUser(user);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    

    @PostMapping
    public ResponseEntity<CategoryDto> createCustomCategory(@Valid @RequestBody CategoryDto categoryDto, 
                                                           HttpSession session) {
        try {
            User user = getCurrentUser(session);
            CategoryDto createdCategory = categoryService.createCustomCategory(categoryDto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> deleteCustomCategory(@PathVariable String name, 
                                                                   HttpSession session) {
        try {
            User user = getCurrentUser(session);
            categoryService.deleteCustomCategory(name, user);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Cannot delete category"));
        }
    }
    

    private User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not authenticated: session is missing or expired");
        }
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalStateException("User not found: invalid userId");
        }
        return user;
    }
}
