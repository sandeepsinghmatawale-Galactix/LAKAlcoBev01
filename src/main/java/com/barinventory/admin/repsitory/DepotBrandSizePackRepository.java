package com.barinventory.admin.repsitory;

//admin/repositories/DepotBrandSizePackRepository.java
 

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotBrandSizePack;
import com.barinventory.admin.enums.EntityStatus;
import com.barinventory.admin.enums.PackagingType;

public interface DepotBrandSizePackRepository extends JpaRepository<DepotBrandSizePack, Long> {
 List<DepotBrandSizePack> findByBrandSize_BrandSizeId(Long brandSizeId);
 Optional<DepotBrandSizePack> findByBrandSize_BrandSizeIdAndPackagingType(Long brandSizeId, PackagingType packagingType);
 List<DepotBrandSizePack> findByVisibleToBarsTrueAndStatusIn(List<EntityStatus> statuses);
}