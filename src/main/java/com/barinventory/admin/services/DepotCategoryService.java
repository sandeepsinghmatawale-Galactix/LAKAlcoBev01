package com.barinventory.admin.services;

//admin/services/DepotCategoryService.java
 

import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.CategoryDto;
import com.barinventory.admin.entities.DepotCategory;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotCategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DepotCategoryService {
 private final DepotCategoryRepository repository;

 @Transactional
 public CategoryDto create(CategoryDto dto) {
     if (repository.existsByCategoryNameIgnoreCase(dto.categoryName()))
         throw new IllegalArgumentException("Category already exists: " + dto.categoryName());
     DepotCategory entity = new DepotCategory();
     entity.setCategoryName(dto.categoryName());
     return toDto(repository.save(entity));
 }

 @Transactional
 public CategoryDto update(Long id, CategoryDto dto) {
     DepotCategory entity = repository.findById(id)
         .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
     entity.setCategoryName(dto.categoryName());
     return toDto(repository.save(entity));
 }

 public List<CategoryDto> getAll() {
     return repository.findAll().stream().map(this::toDto).toList();
 }

 private CategoryDto toDto(DepotCategory c) {
     return new CategoryDto(c.getCategoryId(), c.getCategoryName());
 }
}