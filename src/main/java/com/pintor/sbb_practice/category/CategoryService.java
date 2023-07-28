package com.pintor.sbb_practice.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getList() {
        return this.categoryRepository.findAll();
    }

    public void create(String type) {
        Category category = new Category();
        category.setType(type);
        this.categoryRepository.save(category);
    }
}