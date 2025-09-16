package com.learning.blog.service.impl;

import com.learning.blog.exception.ResourceNotFoundException;
import com.learning.blog.mapper.UserMapper;
import com.learning.blog.model.User;
import com.learning.blog.model.dtos.UserRequest;
import com.learning.blog.model.dtos.UserResponse;
import com.learning.blog.repository.UserRepository;
import com.learning.blog.service.UserService;
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
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            return userMapper.toResponse(user);
        } catch (ResourceNotFoundException e) {
            log.error("Error during registration for id: {}, error: {}", id, e.getMessage());
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
            return userMapper.toResponse(user);
        } catch (ResourceNotFoundException e) {
            log.error("Error during registration for email: {}, error: {}", email, e.getMessage());
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        try {
            Page<User> users = userRepository.findAll(pageable);
            return users.map(userMapper::toResponse);
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

            user = userMapper.updateEntity(userRequest);

            User updatedUser = userRepository.save(user);

            return userMapper.toResponse(updatedUser);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public UserResponse updateUserRole(UUID id, String role) {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public void deleteUserByEmail(String email) {

    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public long getUsersCount() {
        return 0;
    }
}

