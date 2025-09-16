package com.learning.blog.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateCategoryRequest {
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 2, max = 50, message = "Category name must be between {min} and {max} characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Category name must contain only alphabetical characters and spaces")
    private String name;
}
