package com.hadaka_electro.internal.user.service;

import com.hadaka_electro.common.entities.User;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.internal.aspect.CheckObjectUniqueness;
import com.hadaka_electro.internal.user.UserDTO;
import com.hadaka_electro.internal.user.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestUserEmailUniqueness {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JoinPoint joinPoint;

    @InjectMocks
    private CheckObjectUniqueness checkObjectUniqueness;

    @Test
    public void testCheckEmailUniqueness_InEditMode_ShouldThrowExceptionWhenDuplicated() {
        int id = 2;
        String email = "ahmed@gmail.com";

        UserDTO userDTO = new UserDTO(id, email);
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setEmail(email);

        when(joinPoint.getArgs()).thenReturn(new Object[]{userDTO});
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        DuplicatedObjectException exception = assertThrows(
                DuplicatedObjectException.class,
                () -> checkObjectUniqueness.checkUserEmailUniqueness(joinPoint)
        );

        assertEquals("The email " + email + " is already taken!", exception.getMessage());
    }

    @Test
    public void testCheckEmailUniqueness_InEditMode_ShouldNotThrowExceptionWhenSameUser() {
        int id = 2;
        String email = "ahmed@gmail.com";

        UserDTO userDTO = new UserDTO(id, email);
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail(email);

        when(joinPoint.getArgs()).thenReturn(new Object[]{userDTO});
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> checkObjectUniqueness.checkUserEmailUniqueness(joinPoint));
    }

    @Test
    public void testCheckEmailUniqueness_InNewUserMode_ShouldThrowExceptionWhenDuplicated() {
        int id = 0;
        String email = "ahmed@gmail.com";

        UserDTO userDTO = new UserDTO(id, email);
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setEmail(email);

        when(joinPoint.getArgs()).thenReturn(new Object[]{userDTO});
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        DuplicatedObjectException exception = assertThrows(
                DuplicatedObjectException.class,
                () -> checkObjectUniqueness.checkUserEmailUniqueness(joinPoint)
        );

        assertEquals("The email " + email + " is already taken!", exception.getMessage());
    }

    @Test
    public void testCheckEmailUniqueness_InNewUserMode_ShouldNotThrowExceptionWhenUnique() {
        int id = 0;
        String email = "another@gmail.com";

        UserDTO userDTO = new UserDTO(id, email);

        when(joinPoint.getArgs()).thenReturn(new Object[]{userDTO});
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> checkObjectUniqueness.checkUserEmailUniqueness(joinPoint));
    }
}