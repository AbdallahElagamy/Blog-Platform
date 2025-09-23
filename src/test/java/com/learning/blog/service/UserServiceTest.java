package com.learning.blog.service;

import com.learning.blog.exception.ResourceNotFoundException;
import com.learning.blog.mapper.UserMapper;
import com.learning.blog.model.User;
import com.learning.blog.model.dtos.UserRequest;
import com.learning.blog.model.dtos.UserResponse;
import com.learning.blog.model.enums.UserRole;
import com.learning.blog.repository.UserRepository;
import com.learning.blog.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("testuser")
                .email("testuser@gmail.com")
                .password("password123")
                .role(UserRole.USER)
                .build();

        userRequest = UserRequest.builder()
                .name("testusernew")
                .email("testusernew@gmail.com")
                .role(UserRole.USER)
                .build();

        userResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .name("testusernew")
                .email("testusernew@gmail.com")
                .build();
    }

    @Test
    void shouldGetUserById() {
        UUID id = user.getId();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenGetUserByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));

        verify(userRepository, times(1)).findById(id);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldGetUserByEmail() {
        String email = "testuser@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenGetUserByEmailNotFound() {
        String email = "testuser@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldGetAllUsers() {
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAllUsers(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(userRepository, times(1)).findAll(any(Pageable.class));
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void shouldGetUsersCount() {
        long expectedCount = 5L;

        when(userRepository.count()).thenReturn(expectedCount);

        long result = userService.getUsersCount();

        assertEquals(expectedCount, result);

        verify(userRepository, times(1)).count();
    }

    @Test
    void shouldUpdateUser() {
        UUID id = user.getId();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(id, userRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserWithExistingEmail() {
        UUID id = user.getId();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(id, userRequest));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserNotFound() {
        UUID id = user.getId();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(id, userRequest));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldUpdateUserRole() {
        UUID id = user.getId();
        String newRole = "ADMIN";

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUserRole(id, newRole);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserRoleWithInvalidRole() {
        UUID id = user.getId();
        String invalidRole = "INVALID_ROLE";

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserRole(id, invalidRole));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserRoleNotFound() {
        UUID id = UUID.randomUUID();
        String role = "ADMIN";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserRole(id, role));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        UUID id = user.getId();

        when(userRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(id));

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeleteUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(id));

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }

    @Test
    void shouldDeleteUserByEmail() {
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUserByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeleteUserByEmailNotFound() {
        String email = "nonexistent@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).delete(any(User.class));
    }
}
