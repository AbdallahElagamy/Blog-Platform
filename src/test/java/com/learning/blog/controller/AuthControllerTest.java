package com.learning.blog.controller;

import com.learning.blog.BlogApplication;
import com.learning.blog.config.SecurityConfig;
import com.learning.blog.model.dtos.AuthResponse;
import com.learning.blog.model.dtos.RegisterRequest;
import com.learning.blog.model.enums.UserRole;
import com.learning.blog.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterUser() throws Exception {
        mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(
                                """
                                        {
                                            "userName": "testuser",
                                            "email": "testuser@gmail.com",
                                            "password": "password123",
                                            "role": "USER"
                                        }
                                """
                        )
        ).andExpect(status().isCreated());

    }

}
