package com.learning.blog.service;

import com.learning.blog.model.dtos.UserRequest;
import com.learning.blog.model.dtos.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(UUID id, UserRequest userRequest);
    UserResponse updateUserRole(UUID id, String role);
    void deleteUser(UUID id);
    void deleteUserByEmail(String email);
    long getUsersCount();
}
