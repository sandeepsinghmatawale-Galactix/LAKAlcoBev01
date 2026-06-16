package com.barinventory.admin.repsitory;

//admin/repositories/DepotManufacturerRepository.java

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotManufacturer;

public interface DepotManufacturerRepository extends JpaRepository<DepotManufacturer, Long> {
}
