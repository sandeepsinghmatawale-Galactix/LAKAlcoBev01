package com.barinventory.admin.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.SubCategoryDto;
import com.barinventory.admin.entities.DepotCategory;
import com.barinventory.admin.entities.DepotSubCategory;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotCategoryRepository;
import com.barinventory.admin.repsitory.DepotSubCategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotSubCategoryService {
	private final DepotSubCategoryRepository repository;
	private final DepotCategoryRepository categoryRepository;

	@Transactional
	public SubCategoryDto create(SubCategoryDto dto) {
		DepotCategory category = categoryRepository.findById(dto.categoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.categoryId()));
		if (repository.existsBySubCategoryNameIgnoreCaseAndCategory_CategoryId(dto.subCategoryName(), dto.categoryId()))
			throw new IllegalArgumentException("SubCategory already exists under this category");
		DepotSubCategory entity = new DepotSubCategory();
		entity.setCategory(category);
		entity.setSubCategoryName(dto.subCategoryName());
		return toDto(repository.save(entity));
	}

	public List<SubCategoryDto> getByCategory(Long categoryId) {
		return repository.findByCategory_CategoryId(categoryId).stream().map(this::toDto).toList();
	}

	public List<SubCategoryDto> getAll() {
		return repository.findAll().stream().map(this::toDto).toList();
	}

	private SubCategoryDto toDto(DepotSubCategory s) {
		return new SubCategoryDto(s.getSubCategoryId(), s.getCategory().getCategoryId(),
				s.getCategory().getCategoryName(), s.getSubCategoryName());
	}
}