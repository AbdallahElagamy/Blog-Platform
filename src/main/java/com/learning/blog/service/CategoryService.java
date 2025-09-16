package com.learning.blog.service;

import com.learning.blog.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> getAllWithPostCount();
    Category createCategory(Category category);
    void deleteCategory(UUID id);
}
