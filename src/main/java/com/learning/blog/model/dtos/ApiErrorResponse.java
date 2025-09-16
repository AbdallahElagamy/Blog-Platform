package com.learning.blog.model.dtos;

import lombok.*;
import org.springframework.validation.FieldError;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
