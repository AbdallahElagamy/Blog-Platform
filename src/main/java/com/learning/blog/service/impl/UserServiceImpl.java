package com.learning.blog.service.impl;

import com.learning.blog.exception.ResourceNotFoundException;
import com.learning.blog.mapper.UserMapper;
import com.learning.blog.model.User;
import com.learning.blog.model.dtos.UserRequest;
import com.learning.blog.model.dtos.UserResponse;
import com.learning.blog.model.enums.UserRole;
import com.learning.blog.repository.UserRepository;
import com.learning.blog.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(UUID id) {
        log.debug("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pageable: {}", pageable);

        try {
            Page<User> users = userRepository.findAll(pageable);

            return users.map(userMapper::toResponse);

        } catch (Exception e) {
            log.error("Unexpected error while fetching users: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while retrieving users", e);
        }
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        log.debug("Updating user with id: {}", id);

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            if (!existingUser.getEmail().equals(userRequest.getEmail()) &&
                    userRepository.existsByEmail(userRequest.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
            }

            existingUser.setName(userRequest.getName());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setRole(userRequest.getRole());

            User updatedUser = userRepository.save(existingUser);

            log.debug("User updated successfully with id: {}", id);
            return userMapper.toResponse(updatedUser);

        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user with id: {}", id);
            throw new RuntimeException("An unexpected error occurred while updating user", e);
        }
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(UUID id, String role) {
        log.debug("Updating user role for id: {} to role: {}", id, role);

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            UserRole userRole = parseUserRole(role);
            existingUser.setRole(userRole);

            User updatedUser = userRepository.save(existingUser);

            log.debug("User role updated successfully for id: {}", id);
            return userMapper.toResponse(updatedUser);

        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating user role for id: {}", id);
            throw new RuntimeException("An unexpected error occurred while updating user role", e);
        }
    }

    private UserRole parseUserRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        return switch (role.toUpperCase().trim()) {
            case "ADMIN" -> UserRole.ADMIN;
            case "USER" -> UserRole.USER;
            default -> throw new IllegalArgumentException("Invalid role: " + role + ". Valid roles are: ADMIN, USER");
        };
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Deleting user with id: {}", id);

        try {
            if (!userRepository.existsById(id)) {
                throw new ResourceNotFoundException("User not found with id: " + id);
            }

            userRepository.deleteById(id);
            log.debug("User deleted successfully with id: {}", id);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with id: {}", id);
            throw new RuntimeException("An unexpected error occurred while deleting user", e);
        }
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        log.debug("Deleting user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

            userRepository.delete(user);
            log.debug("User deleted successfully with email: {}", email);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting user with email: {}", email);
            throw new RuntimeException("An unexpected error occurred while deleting user", e);
        }
    }

    @Override
    public long getUsersCount() {
        log.debug("Getting total users count");

        try {
            return userRepository.count();
        } catch (Exception e) {
            log.error("Unexpected error while getting users count");
            throw new RuntimeException("An unexpected error occurred while counting users", e);
        }
    }
}
