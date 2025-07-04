package com.financemanager.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.financemanager.entity.User;

@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void findByUsername_Success() {
        User user = new User("test@example.com", "password", "John Doe", "+1234567890");
        entityManager.persistAndFlush(user);
        
        Optional<User> found = userRepository.findByUsername("test@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getUsername());
    }
    
    @Test
    void findByUsername_NotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }
    
    @Test
    void existsByUsername_True() {
        User user = new User("test@example.com", "password", "John Doe", "+1234567890");
        entityManager.persistAndFlush(user);
        
        boolean exists = userRepository.existsByUsername("test@example.com");
        
        assertTrue(exists);
    }
    
    @Test
    void existsByUsername_False() {
        boolean exists = userRepository.existsByUsername("nonexistent@example.com");
        
        assertFalse(exists);
    }
    
    @Test
    void existsByPhoneNumber_True() {
        User user = new User("test@example.com", "password", "John Doe", "+1234567890");
        entityManager.persistAndFlush(user);
        
        boolean exists = userRepository.existsByPhoneNumber("+1234567890");
        
        assertTrue(exists);
    }
    
    @Test
    void existsByPhoneNumber_False() {
        boolean exists = userRepository.existsByPhoneNumber("+9999999999");
        
        assertFalse(exists);
    }
}
