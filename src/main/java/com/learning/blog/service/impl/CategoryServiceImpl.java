package com.learning.blog.service.impl;

import com.learning.blog.model.Category;
import com.learning.blog.repository.CategoryRepository;
import com.learning.blog.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    public List<Category> getAllWithPostCount() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        String name = category.getName();
        if(categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category with name " + name + " already exists");
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if(categoryOpt.isPresent()) {
            if(!categoryOpt.get().getPosts().isEmpty()) {
                throw new IllegalStateException("Category with id " + id + " has posts and cannot be deleted");
            }
            categoryRepository.deleteById(id);
        }
    }
}
