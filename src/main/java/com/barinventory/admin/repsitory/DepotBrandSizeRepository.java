package com.barinventory.admin.repsitory;
//admin/repositories/DepotBrandSizeRepository.java

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotBrandSize;

public interface DepotBrandSizeRepository extends JpaRepository<DepotBrandSize, Long> {
	List<DepotBrandSize> findByBrand_BrandId(Long brandId);

	Optional<DepotBrandSize> findByBrand_BrandIdAndSizeMl(Long brandId, Integer sizeMl);
}