package com.financemanager.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.financemanager.dto.UserLoginDto;
import com.financemanager.dto.UserRegistrationDto;
import com.financemanager.entity.User;
import com.financemanager.exception.DuplicateResourceException;
import com.financemanager.exception.InvalidCredentialsException;
import com.financemanager.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private UserRegistrationDto registrationDto;
    private UserLoginDto loginDto;
    private User user;
    
    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto(
            "test@example.com", "password123", "John Doe", "+1234567890"
        );
        
        loginDto = new UserLoginDto("test@example.com", "password123");
        
        user = new User("test@example.com", "encodedPassword", "John Doe", "+1234567890");
        user.setId(1L);
    }
    
    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User result = userService.registerUser(registrationDto);
        
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_DuplicateUsername() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> {
            userService.registerUser(registrationDto);
        });
    }
    
    @Test
    void registerUser_DuplicatePhoneNumber() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> {
            userService.registerUser(registrationDto);
        });
    }
    
    @Test
    void authenticateUser_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        
        User result = userService.authenticateUser(loginDto);
        
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }
    
    @Test
    void authenticateUser_InvalidUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.authenticateUser(loginDto);
        });
    }
    
    @Test
    void authenticateUser_InvalidPassword() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.authenticateUser(loginDto);
        });
    }
    
    @Test
    void findByUsername_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        
        User result = userService.findByUsername("test@example.com");
        
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }
    
    @Test
    void findById_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        
        User result = userService.findById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
