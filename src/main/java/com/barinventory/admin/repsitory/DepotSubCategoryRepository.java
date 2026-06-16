package com.barinventory.admin.repsitory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotSubCategory;

public interface DepotSubCategoryRepository extends JpaRepository<DepotSubCategory, Long> {
	List<DepotSubCategory> findByCategory_CategoryId(Long categoryId);

	boolean existsBySubCategoryNameIgnoreCaseAndCategory_CategoryId(String name, Long categoryId);
}