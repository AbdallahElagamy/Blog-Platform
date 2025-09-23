package com.learning.blog.mapper;

import com.learning.blog.model.User;
import com.learning.blog.model.dtos.RegisterRequest;
import com.learning.blog.model.dtos.UserRequest;
import com.learning.blog.model.dtos.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "posts", ignore = true)
    User toEntity(RegisterRequest registerRequest);
    User toEntity(UserRequest userRequest);
    UserResponse toResponse(User user);

    User updateEntity(UserRequest userRequest);
}
