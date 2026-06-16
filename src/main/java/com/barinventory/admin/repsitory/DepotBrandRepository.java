package com.barinventory.admin.repsitory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotBrand;

public interface DepotBrandRepository extends JpaRepository<DepotBrand, Long> {
	Optional<DepotBrand> findByBrandNameIgnoreCase(String brandName);

	boolean existsByBrandNameIgnoreCase(String brandName);
}