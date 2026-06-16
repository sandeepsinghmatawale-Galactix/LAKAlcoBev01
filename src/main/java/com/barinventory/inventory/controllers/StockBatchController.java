package com.barinventory.inventory.controllers;

//inventory/controllers/StockBatchController.java
 

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barinventory.inventory.dtos.StockBatchResponse;
import com.barinventory.inventory.dtos.StockOnHandResponse;
import com.barinventory.inventory.dtos.StockReceiptRequest;
import com.barinventory.inventory.entities.StockBatch;
import com.barinventory.inventory.services.StockCostingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory/stock")
@RequiredArgsConstructor
public class StockBatchController {
 private final StockCostingService costingService;

 @PostMapping("/receipts")
 public StockBatchResponse recordReceipt(@RequestBody StockReceiptRequest req) {
     return toResponse(costingService.recordReceipt(req));
 }

 @GetMapping("/on-hand")
 public StockOnHandResponse getOnHand(@RequestParam Long barId, @RequestParam Long depotPackId) {
     return new StockOnHandResponse(barId, depotPackId, costingService.getStockOnHand(barId, depotPackId));
 }

 @GetMapping("/batches")
 public List<StockBatchResponse> getBatches(@RequestParam Long barId, @RequestParam Long depotPackId) {
     return costingService.getBatchHistory(barId, depotPackId).stream().map(this::toResponse).toList();
 }

 private StockBatchResponse toResponse(StockBatch b) {
     return new StockBatchResponse(b.getId(), b.getBarId(), b.getDepotPackId(),
         b.getQuantityReceived(), b.getQuantityRemaining(), b.getPurchasePricePerUnit(),
         b.getInvoiceRefNo(), b.getReceivedAt());
 }
}