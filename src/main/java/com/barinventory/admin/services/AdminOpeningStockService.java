package com.barinventory.admin.services;

//admin/services/AdminOpeningStockService.java
 

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barinventory.admin.dtos.AdminOpeningStockRequest;
import com.barinventory.admin.dtos.AdminOpeningStockResponse;
import com.barinventory.admin.dtos.BarWellsResponse;
import com.barinventory.admin.dtos.StockroomOpeningLine;
import com.barinventory.admin.dtos.WellOpeningLine;
import com.barinventory.admin.enums.BarPricingStatus;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.auth.entities.Bar;
import com.barinventory.auth.repos.BarRepository;
import com.barinventory.inventory.entities.BarProductPricing;
import com.barinventory.inventory.entities.InventoryStatus;
import com.barinventory.inventory.entities.StockBatch;
import com.barinventory.inventory.entities.StockroomInventory;
import com.barinventory.inventory.entities.Well;
import com.barinventory.inventory.entities.WellInventory;
import com.barinventory.inventory.repos.BarProductPricingRepository;
import com.barinventory.inventory.repos.StockBatchRepository;
import com.barinventory.inventory.repos.StockroomInventoryRepository;
import com.barinventory.inventory.repos.WellInventoryRepository;
import com.barinventory.inventory.repos.WellRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminOpeningStockService {

 private final BarRepository barRepository;
 private final WellRepository wellRepo;
 private final BarProductPricingRepository pricingRepo;
 private final StockroomInventoryRepository stockroomRepo;
 private final WellInventoryRepository wellInventoryRepo;
 private final StockBatchRepository stockBatchRepo;

 /**
  * Returns all wells for a bar so admin UI can display them
  * before entering opening stock.
  */
 public BarWellsResponse getBarWells(Long barId) {
     Bar bar = barRepository.findById(barId)
         .orElseThrow(() -> new ResourceNotFoundException("Bar not found: " + barId));

     List<BarWellsResponse.WellDto> wells = wellRepo.findByBarId(barId)
         .stream()
         .map(w -> new BarWellsResponse.WellDto(w.getWellId(), w.getWellName()))
         .toList();

     return new BarWellsResponse(barId, bar.getBarName(), wells);
 }

 /**
  * Admin enters opening stock for stockroom + all wells of a bar.
  *
  * Steps per stockroom line:
  * 1. Upsert BarProductPricing (so bar owner sees SKU immediately)
  * 2. Create StockBatch (FIFO cost tracking)
  * 3. Create StockroomInventory with session=null (opening stock, no session yet)
  *
  * Steps per well line:
  * 1. Validate well belongs to this bar
  * 2. Validate BarProductPricing exists (must have been created in stockroom step)
  * 3. Create WellInventory with session=null, status=IN_PROGRESS
  */
 @Transactional
 public AdminOpeningStockResponse seedOpeningStock(AdminOpeningStockRequest req) {
     Bar bar = barRepository.findById(req.barId())
         .orElseThrow(() -> new ResourceNotFoundException("Bar not found: " + req.barId()));

     // Validate all wells belong to this bar upfront
     List<Well> barWells = wellRepo.findByBarId(req.barId());
     Map<Long, Well> wellMap = barWells.stream()
         .collect(Collectors.toMap(Well::getWellId, w -> w));

     if (req.wellLines() != null) {
         for (WellOpeningLine line : req.wellLines()) {
             if (!wellMap.containsKey(line.wellId())) {
                 throw new IllegalArgumentException(
                     "Well " + line.wellId() + " does not belong to bar " + req.barId());
             }
         }
     }

     // --- STOCKROOM ---
     int stockroomCount = 0;
     if (req.stockroomLines() != null) {
         for (StockroomOpeningLine line : req.stockroomLines()) {
             // 1. Upsert BarProductPricing
             BarProductPricing pricing = pricingRepo
                 .findByBarIdAndDepotPackId(req.barId(), line.depotPackId())
                 .orElseGet(() -> {
                     BarProductPricing p = new BarProductPricing();
                     p.setBarId(req.barId());
                     p.setDepotBrandSizeId(line.depotBrandSizeId());
                     p.setDepotPackId(line.depotPackId());
                     p.setDepotBrandId(null); // resolved via depotBrandSizeId if needed
                     p.setCachedBrandName(line.brandName());
                     p.setCachedSizeMl(line.sizeMl());
                     p.setCachedPackagingType(line.packagingType());
                     p.setCachedMrp(line.mrp());
                     p.setPurchasePrice(line.purchasePricePerUnit());
                     p.setSellingPrice(line.sellingPrice() != null
                         ? line.sellingPrice() : line.mrp());
                     p.setPriceLockedToMrp(line.sellingPrice() == null);
                     p.setStatus(BarPricingStatus.ACTIVE);
                     p.setCreatedAt(LocalDateTime.now());
                     p.setUpdatedAt(LocalDateTime.now());
                     return pricingRepo.save(p);
                 });

             // Update purchase price if already exists
             pricing.setPurchasePrice(line.purchasePricePerUnit());
             pricing.setUpdatedAt(LocalDateTime.now());
             pricingRepo.save(pricing);

             // 2. StockBatch for FIFO
             StockBatch batch = new StockBatch();
             batch.setBarId(req.barId());
             batch.setDepotPackId(line.depotPackId());
             batch.setQuantityReceived(line.openingQty());
             batch.setQuantityRemaining(line.openingQty());
             batch.setPurchasePricePerUnit(line.purchasePricePerUnit());
             batch.setInvoiceRefNo(line.invoiceRefNo() != null
                 ? line.invoiceRefNo() : "OPENING-STOCK");
             batch.setReceivedAt(LocalDateTime.now());
             batch.setCreatedAt(LocalDateTime.now());
             stockBatchRepo.save(batch);

             // 3. StockroomInventory — session null (opening stock)
             // Skip if already seeded for this bar+SKU with no session
             boolean alreadySeeded = stockroomRepo
                 .findByBarIdAndDepotBrandSizeIdAndSessionIsNull(req.barId(), line.depotBrandSizeId())
                 .isPresent();

             if (!alreadySeeded) {
                 StockroomInventory stockroom = new StockroomInventory();
                 stockroom.setBarId(req.barId());
                 stockroom.setDepotBrandSizeId(line.depotBrandSizeId());
                 stockroom.setSession(null); // no session yet
                 stockroom.setOpeningStock(line.openingQty());
                 stockroom.setReceivedStock(0);
                 stockroom.setClosingStock(line.openingQty()); // closing = opening until session starts
                 stockroom.setSaleStock(0);
                 stockroomRepo.save(stockroom);
             } else {
                 // Update existing opening stock row
                 stockroomRepo.findByBarIdAndDepotBrandSizeIdAndSessionIsNull(
                     req.barId(), line.depotBrandSizeId())
                     .ifPresent(s -> {
                         s.setOpeningStock(line.openingQty());
                         s.setClosingStock(line.openingQty());
                         stockroomRepo.save(s);
                     });
             }
             stockroomCount++;
         }
     }

     // --- WELLS ---
     int wellLinesCount = 0;
     Map<Long, Integer> wellItemCount = new java.util.HashMap<>();
     // ↑ simpler:
     

     if (req.wellLines() != null) {
         for (WellOpeningLine line : req.wellLines()) {
             Well well = wellMap.get(line.wellId());

             // BarProductPricing must exist — created in stockroom step above
             BarProductPricing pricing = pricingRepo
                 .findByBarIdAndDepotPackId(req.barId(), line.depotPackId())
                 .orElseThrow(() -> new IllegalArgumentException(
                     "No pricing found for packId " + line.depotPackId() +
                     ". Add this SKU to stockroom opening stock first."));

             // Skip if already seeded for this well+SKU with no session
             boolean alreadySeeded = wellInventoryRepo
                 .findByBarIdAndWellWellIdAndProductPricingIdAndSessionIsNull(
                     req.barId(), line.wellId(), pricing.getId())
                 .isPresent();

             if (!alreadySeeded) {
                 WellInventory wi = new WellInventory();
                 wi.setBarId(req.barId());
                 wi.setWell(well);
                 wi.setProductPricing(pricing);
                 wi.setSession(null); // no session yet
                 wi.setOpeningStock(line.openingQty());
                 wi.setReceivedStock(0);
                 wi.setClosingStock(line.openingQty());
                 wi.setSaleStock(0);
                 wi.setAmount(BigDecimal.ZERO);
                 wi.setStatus(InventoryStatus.IN_PROGRESS);
                 wellInventoryRepo.save(wi);
             } else {
                 wellInventoryRepo
                     .findByBarIdAndWellWellIdAndProductPricingIdAndSessionIsNull(
                         req.barId(), line.wellId(), pricing.getId())
                     .ifPresent(wi -> {
                         wi.setOpeningStock(line.openingQty());
                         wi.setClosingStock(line.openingQty());
                         wellInventoryRepo.save(wi);
                     });
             }

             wellItemCount.merge(line.wellId(), 1, Integer::sum);
             wellLinesCount++;
         }
     }

     // Build well summaries
     List<AdminOpeningStockResponse.WellSummary> wellSummaries = wellItemCount.entrySet()
         .stream()
         .map(e -> new AdminOpeningStockResponse.WellSummary(
             e.getKey(),
             wellMap.get(e.getKey()).getWellName(),
             e.getValue()
         )).toList();

     return new AdminOpeningStockResponse(
         req.barId(), bar.getBarName(),
         stockroomCount, wellLinesCount, wellSummaries
     );
 }
}