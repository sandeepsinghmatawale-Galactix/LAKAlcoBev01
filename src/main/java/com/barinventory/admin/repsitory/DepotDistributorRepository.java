package com.barinventory.admin.repsitory;

//admin/repositories/DepotDistributorRepository.java

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.admin.entities.DepotDistributor;

public interface DepotDistributorRepository extends JpaRepository<DepotDistributor, Long> {
}