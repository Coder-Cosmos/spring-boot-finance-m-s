package com.financemanager.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financemanager.dto.UserLoginDto;
import com.financemanager.dto.UserRegistrationDto;
import com.financemanager.entity.User;
import com.financemanager.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void registerUser_Success() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
            "test@example.com", "password123", "John Doe", "+1234567890"
        );
        
        User user = new User("test@example.com", "password123", "John Doe", "+1234567890");
        user.setId(1L);
        
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(user);
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(1));
    }
    
    @Test
    void registerUser_InvalidInput() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
            "", "", "", ""
        );
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void loginUser_Success() throws Exception {
        UserLoginDto loginDto = new UserLoginDto("test@example.com", "password123");
        
        User user = new User("test@example.com", "password123", "John Doe", "+1234567890");
        user.setId(1L);
        
        when(userService.authenticateUser(any(UserLoginDto.class))).thenReturn(user);
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
    
    @Test
    void logoutUser_Success() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}
