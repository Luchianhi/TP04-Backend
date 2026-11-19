package com.example.shop.service;

import com.example.shop.dto.CategoryDto;
import com.example.shop.entity.Category;
import com.example.shop.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepo;

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        Category c = new Category();
        c.setName(dto.name());
        Category saved = categoryRepo.save(c);
        return new CategoryDto(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category c = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return new CategoryDto(c.getId(), c.getName());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryRepo.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName())).toList();
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category c = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        c.setName(dto.name());
        Category saved = categoryRepo.save(c);
        return new CategoryDto(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(Long id) {
        categoryRepo.deleteById(id);
    }
}
