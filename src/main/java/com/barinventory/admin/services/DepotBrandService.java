package com.barinventory.admin.services;

//admin/services/DepotBrandService.java
 

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.DepotBrandRequest;
import com.barinventory.admin.dtos.DepotBrandResponse;
import com.barinventory.admin.dtos.StatusUpdateRequest;
import com.barinventory.admin.entities.DepotBrand;
import com.barinventory.admin.enums.EntityStatus;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotBrandRepository;
import com.barinventory.admin.repsitory.DepotCategoryRepository;
import com.barinventory.admin.repsitory.DepotDistributorRepository;
import com.barinventory.admin.repsitory.DepotManufacturerRepository;
import com.barinventory.admin.repsitory.DepotSubCategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
 

@Service
@RequiredArgsConstructor
public class DepotBrandService {
 private final DepotBrandRepository brandRepository;
 private final DepotCategoryRepository categoryRepository;
 private final DepotSubCategoryRepository subCategoryRepository;
 private final DepotManufacturerRepository manufacturerRepository;
 private final DepotDistributorRepository distributorRepository;

 @Transactional
 public DepotBrandResponse create(DepotBrandRequest req) {
     if (brandRepository.existsByBrandNameIgnoreCase(req.brandName()))
         throw new IllegalArgumentException("Brand already exists: " + req.brandName());
     DepotBrand brand = new DepotBrand();
     applyRequest(brand, req);
     brand.setStatus(EntityStatus.ACTIVE);
     brand.setVisibleToBars(true);
     brand.setCreatedAt(LocalDateTime.now());
     brand.setUpdatedAt(LocalDateTime.now());
     return toResponse(brandRepository.save(brand));
 }

 @Transactional
 public DepotBrandResponse update(Long brandId, DepotBrandRequest req) {
     DepotBrand brand = getEntity(brandId);
     applyRequest(brand, req);
     brand.setUpdatedAt(LocalDateTime.now());
     return toResponse(brandRepository.save(brand));
 }

 @Transactional
 public DepotBrandResponse updateStatus(Long brandId, StatusUpdateRequest req) {
     DepotBrand brand = getEntity(brandId);
     if (req.status() != null) brand.setStatus(EntityStatus.valueOf(req.status()));
     if (req.visibleToBars() != null) brand.setVisibleToBars(req.visibleToBars());
     brand.setUpdatedAt(LocalDateTime.now());
     return toResponse(brandRepository.save(brand));
 }

 public DepotBrandResponse getById(Long brandId) {
     return toResponse(getEntity(brandId));
 }

 public List<DepotBrandResponse> getAll() {
     return brandRepository.findAll().stream().map(this::toResponse).toList();
 }

 private DepotBrand getEntity(Long brandId) {
     return brandRepository.findById(brandId)
         .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + brandId));
 }

 private void applyRequest(DepotBrand brand, DepotBrandRequest req) {
     brand.setBrandName(req.brandName());
     brand.setCategory(categoryRepository.findById(req.categoryId())
         .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.categoryId())));
     brand.setSubCategory(subCategoryRepository.findById(req.subCategoryId())
         .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found: " + req.subCategoryId())));

     if (req.manufacturerId() != null) {
         brand.setManufacturer(manufacturerRepository.findById(req.manufacturerId())
             .orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found: " + req.manufacturerId())));
     } else {
         brand.setManufacturer(null);
     }

     if (req.distributorId() != null) {
         brand.setDistributor(distributorRepository.findById(req.distributorId())
             .orElseThrow(() -> new ResourceNotFoundException("Distributor not found: " + req.distributorId())));
     } else {
         brand.setDistributor(null);
     }

     brand.setCountryOfOrigin(req.countryOfOrigin());
     brand.setAbv(req.abv());
     brand.setDescription(req.description());
     brand.setImageUrl(req.imageUrl());
 }

 private DepotBrandResponse toResponse(DepotBrand b) {
     return new DepotBrandResponse(
         b.getBrandId(), b.getBrandName(),
         b.getCategory().getCategoryId(), b.getCategory().getCategoryName(),
         b.getSubCategory().getSubCategoryId(), b.getSubCategory().getSubCategoryName(),
         b.getManufacturer() != null ? b.getManufacturer().getManufacturerId() : null,
         b.getManufacturer() != null ? b.getManufacturer().getManufacturerName() : null,
         b.getDistributor() != null ? b.getDistributor().getDistributorId() : null,
         b.getDistributor() != null ? b.getDistributor().getDistributorName() : null,
         b.getCountryOfOrigin(), b.getAbv(), b.getStatus().name(),
         b.getVisibleToBars(), b.getDescription(), b.getImageUrl()
     );
 }
}