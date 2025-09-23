package com.learning.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BlogApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void applicationStartsWithH2Database() {
    }
}

