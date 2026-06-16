package com.barinventory.admin.services;

//admin/services/DepotBrandSizePackService.java


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.DepotBrandSizePackRequest;
import com.barinventory.admin.dtos.DepotBrandSizePackResponse;
import com.barinventory.admin.dtos.StatusUpdateRequest;
import com.barinventory.admin.entities.DepotBrandSize;
import com.barinventory.admin.entities.DepotBrandSizePack;
import com.barinventory.admin.entities.DepotPackPriceHistory;
import com.barinventory.admin.enums.EntityStatus;
import com.barinventory.admin.enums.PackagingType;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotBrandSizePackRepository;
import com.barinventory.admin.repsitory.DepotBrandSizeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotBrandSizePackService {
 private final DepotBrandSizePackRepository packRepository;
 private final DepotBrandSizeRepository sizeRepository;
 private final DepotPackPriceHistoryService priceHistoryService;

 @Transactional
 public DepotBrandSizePackResponse create(Long brandSizeId, DepotBrandSizePackRequest req) {
     DepotBrandSize size = sizeRepository.findById(brandSizeId)
         .orElseThrow(() -> new ResourceNotFoundException("BrandSize not found: " + brandSizeId));

     PackagingType packagingType = PackagingType.valueOf(req.packagingType());
     packRepository.findByBrandSize_BrandSizeIdAndPackagingType(brandSizeId, packagingType)
         .ifPresent(p -> { throw new IllegalArgumentException("Pack already exists for this size+packaging: " + packagingType); });

     DepotBrandSizePack pack = new DepotBrandSizePack();
     pack.setBrandSize(size);
     pack.setPackagingType(packagingType);
     pack.setUnitsPerCase(req.unitsPerCase());
     pack.setBarcode(req.barcode());
     pack.setHsnCode(req.hsnCode());
     pack.setStatus(EntityStatus.NEW_LAUNCH);
     pack.setVisibleToBars(true);
     pack.setCreatedAt(LocalDateTime.now());
     pack.setUpdatedAt(LocalDateTime.now());
     return toResponse(packRepository.save(pack));
 }

 @Transactional
 public DepotBrandSizePackResponse updateStatus(Long packId, StatusUpdateRequest req) {
     DepotBrandSizePack pack = getEntity(packId);
     if (req.status() != null) pack.setStatus(EntityStatus.valueOf(req.status()));
     if (req.visibleToBars() != null) pack.setVisibleToBars(req.visibleToBars());
     pack.setUpdatedAt(LocalDateTime.now());
     return toResponse(packRepository.save(pack));
 }

 public List<DepotBrandSizePackResponse> getByBrandSize(Long brandSizeId) {
     return packRepository.findByBrandSize_BrandSizeId(brandSizeId).stream().map(this::toResponse).toList();
 }

 public DepotBrandSizePackResponse getById(Long packId) {
     return toResponse(getEntity(packId));
 }

 private DepotBrandSizePack getEntity(Long id) {
     return packRepository.findById(id)
         .orElseThrow(() -> new ResourceNotFoundException("Pack not found: " + id));
 }

 private DepotBrandSizePackResponse toResponse(DepotBrandSizePack p) {
     var currentPrice = priceHistoryService.getCurrentPrice(p.getPackId());
     return new DepotBrandSizePackResponse(
         p.getPackId(), p.getBrandSize().getBrandSizeId(),
         p.getBrandSize().getBrand().getBrandName(), p.getBrandSize().getSizeMl(),
         p.getPackagingType().name(), p.getUnitsPerCase(), p.getBarcode(), p.getHsnCode(),
         p.getStatus().name(), p.getVisibleToBars(),
         currentPrice.map(DepotPackPriceHistory::getMrp).orElse(null),
         currentPrice.map(DepotPackPriceHistory::getEffectiveFrom).orElse(null)
     );
 }
}