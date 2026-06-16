package com.barinventory.admin.services;
//admin/services/DepotBrandSizeService.java
 

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.DepotBrandSizeRequest;
import com.barinventory.admin.dtos.DepotBrandSizeResponse;
import com.barinventory.admin.dtos.StatusUpdateRequest;
import com.barinventory.admin.entities.DepotBrand;
import com.barinventory.admin.entities.DepotBrandSize;
import com.barinventory.admin.enums.EntityStatus;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotBrandRepository;
import com.barinventory.admin.repsitory.DepotBrandSizeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotBrandSizeService {
 private final DepotBrandSizeRepository sizeRepository;
 private final DepotBrandRepository brandRepository;

 @Transactional
 public DepotBrandSizeResponse create(Long brandId, DepotBrandSizeRequest req) {
     DepotBrand brand = brandRepository.findById(brandId)
         .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + brandId));

     sizeRepository.findByBrand_BrandIdAndSizeMl(brandId, req.sizeMl())
         .ifPresent(s -> { throw new IllegalArgumentException("Size already exists for this brand: " + req.sizeMl() + "ml"); });

     DepotBrandSize size = new DepotBrandSize();
     size.setBrand(brand);
     size.setSizeMl(req.sizeMl());
     size.setStatus(EntityStatus.NEW_LAUNCH);
     size.setVisibleToBars(true);
     size.setLaunchDate(LocalDateTime.now());
     size.setCreatedAt(LocalDateTime.now());
     size.setUpdatedAt(LocalDateTime.now());
     return toResponse(sizeRepository.save(size));
 }

 @Transactional
 public DepotBrandSizeResponse updateStatus(Long brandSizeId, StatusUpdateRequest req) {
     DepotBrandSize size = getEntity(brandSizeId);
     if (req.status() != null) {
         EntityStatus status = EntityStatus.valueOf(req.status());
         size.setStatus(status);
         if (status == EntityStatus.DISCONTINUED) size.setDiscontinuedDate(LocalDateTime.now());
     }
     if (req.visibleToBars() != null) size.setVisibleToBars(req.visibleToBars());
     size.setUpdatedAt(LocalDateTime.now());
     return toResponse(sizeRepository.save(size));
 }

 public List<DepotBrandSizeResponse> getByBrand(Long brandId) {
     return sizeRepository.findByBrand_BrandId(brandId).stream().map(this::toResponse).toList();
 }

 public DepotBrandSizeResponse getById(Long brandSizeId) {
     return toResponse(getEntity(brandSizeId));
 }

 private DepotBrandSize getEntity(Long id) {
     return sizeRepository.findById(id)
         .orElseThrow(() -> new ResourceNotFoundException("BrandSize not found: " + id));
 }

 private DepotBrandSizeResponse toResponse(DepotBrandSize s) {
     return new DepotBrandSizeResponse(
         s.getBrandSizeId(), s.getBrand().getBrandId(), s.getBrand().getBrandName(),
         s.getSizeMl(), s.getStatus().name(), s.getVisibleToBars(),
         s.getLaunchDate(), s.getDiscontinuedDate()
     );
 }
}