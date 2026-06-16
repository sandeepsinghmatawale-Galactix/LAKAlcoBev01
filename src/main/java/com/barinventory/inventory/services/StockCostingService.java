package com.barinventory.inventory.services;

//inventory/services/StockCostingService.java


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.inventory.dtos.ConsumptionResult;
import com.barinventory.inventory.dtos.StockReceiptRequest;
import com.barinventory.inventory.entities.StockBatch;
import com.barinventory.inventory.exceptions.InsufficientStockException;
import com.barinventory.inventory.repos.StockBatchRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockCostingService {
 private final StockBatchRepository batchRepository;

 @Transactional
 public StockBatch recordReceipt(StockReceiptRequest req) {
     StockBatch batch = new StockBatch();
     batch.setBarId(req.barId());
     batch.setDepotPackId(req.depotPackId());
     batch.setQuantityReceived(req.quantity());
     batch.setQuantityRemaining(req.quantity());
     batch.setPurchasePricePerUnit(req.purchasePricePerUnit());
     batch.setInvoiceRefNo(req.invoiceRefNo());
     batch.setReceivedAt(req.receivedAt() != null ? req.receivedAt() : LocalDateTime.now());
     batch.setCreatedAt(LocalDateTime.now());
     return batchRepository.save(batch);
 }

 /**
  * Consumes stock FIFO across batches for a sale. Returns the actual cost
  * to use for profit calculation (weighted across batches if a sale spans
  * more than one batch's remaining quantity).
  */
 @Transactional
 public ConsumptionResult consumeForSale(Long barId, Long depotPackId, Integer quantity) {
     List<StockBatch> batches = batchRepository.findRemainingBatchesFifo(barId, depotPackId);

     int remainingToConsume = quantity;
     double totalCost = 0.0;

     for (StockBatch batch : batches) {
         if (remainingToConsume <= 0) break;
         int takeFromBatch = Math.min(batch.getQuantityRemaining(), remainingToConsume);
         totalCost += takeFromBatch * batch.getPurchasePricePerUnit();
         batch.setQuantityRemaining(batch.getQuantityRemaining() - takeFromBatch);
         remainingToConsume -= takeFromBatch;
         batchRepository.save(batch);
     }

     if (remainingToConsume > 0) {
         throw new InsufficientStockException(
             "Insufficient stock for packId " + depotPackId + ": short by " + remainingToConsume + " units");
     }

     double avgUnitCost = totalCost / quantity;
     return new ConsumptionResult(quantity, totalCost, avgUnitCost);
 }

 public Integer getStockOnHand(Long barId, Long depotPackId) {
     return batchRepository.getStockOnHand(barId, depotPackId);
 }

 public List<StockBatch> getBatchHistory(Long barId, Long depotPackId) {
     return batchRepository.findByBarIdAndDepotPackIdOrderByReceivedAtDesc(barId, depotPackId);
 }
}