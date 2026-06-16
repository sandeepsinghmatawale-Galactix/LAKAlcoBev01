package com.barinventory.admin.repsitory;

//admin/repositories/DepotCategoryRepository.java

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotCategory;

public interface DepotCategoryRepository extends JpaRepository<DepotCategory, Long> {
	Optional<DepotCategory> findByCategoryNameIgnoreCase(String categoryName);

	boolean existsByCategoryNameIgnoreCase(String categoryName);
}