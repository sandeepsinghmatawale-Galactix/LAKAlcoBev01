package com.barinventory.inventory.services;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.barinventory.inventory.dtos.ConsumptionResult;
import com.barinventory.inventory.dtos.SaleInvoiceRequest;
import com.barinventory.inventory.dtos.SaleInvoiceResponse;
import com.barinventory.inventory.dtos.SaleLineRequest;
import com.barinventory.inventory.dtos.SaleLineResponse;
import com.barinventory.inventory.entities.BarProductPricing;
import com.barinventory.inventory.entities.SaleInvoice;
import com.barinventory.inventory.entities.SaleInvoiceLine;
import com.barinventory.inventory.exceptions.ResourceNotFoundException;
import com.barinventory.inventory.repos.BarProductPricingRepository;
import com.barinventory.inventory.repos.SaleInvoiceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SaleInvoiceService {
 private final SaleInvoiceRepository invoiceRepository;
 private final BarProductPricingRepository pricingRepository;
 private final StockCostingService costingService;

 @Transactional
 public SaleInvoiceResponse createSale(SaleInvoiceRequest req) {
     if (req.lines() == null || req.lines().isEmpty())
         throw new IllegalArgumentException("Sale must contain at least one line item");

     SaleInvoice invoice = new SaleInvoice();
     invoice.setBarId(req.barId());
     invoice.setInvoiceNumber(req.invoiceNumber());
     invoice.setSaleTimestamp(req.saleTimestamp() != null ? req.saleTimestamp() : LocalDateTime.now());
     invoice.setCreatedAt(LocalDateTime.now());

     double totalAmount = 0.0;
     double totalProfit = 0.0;

     for (SaleLineRequest lineReq : req.lines()) {
         BarProductPricing pricing = pricingRepository.findByBarIdAndDepotPackId(req.barId(), lineReq.depotPackId())
             .orElseThrow(() -> new ResourceNotFoundException(
                 "Bar has not configured pricing for packId " + lineReq.depotPackId()));

         ConsumptionResult consumption = costingService.consumeForSale(req.barId(), lineReq.depotPackId(), lineReq.quantity());

         SaleInvoiceLine line = new SaleInvoiceLine();
         line.setInvoice(invoice);
         line.setDepotPackId(lineReq.depotPackId());
         line.setQuantity(lineReq.quantity());
         line.setSnapshotMrp(pricing.getCachedMrp());
         line.setSnapshotSellingPrice(pricing.getSellingPrice());
         line.setSnapshotPurchaseCost(consumption.avgUnitCost());

         double lineTotal = pricing.getSellingPrice() * lineReq.quantity();
         double lineProfit = (pricing.getSellingPrice() - consumption.avgUnitCost()) * lineReq.quantity();
         line.setLineTotal(lineTotal);
         line.setLineProfit(lineProfit);

         line.setCachedBrandName(pricing.getCachedBrandName());
         line.setCachedSizeMl(pricing.getCachedSizeMl());
         line.setCachedPackagingType(pricing.getCachedPackagingType());

         invoice.getLines().add(line);
        
         totalAmount += lineTotal;
         totalProfit += lineProfit;
     }

     invoice.setTotalAmount(totalAmount);
     invoice.setTotalProfit(totalProfit);
     return toResponse(invoiceRepository.save(invoice));
 }

 public SaleInvoiceResponse getById(Long id) {
     return invoiceRepository.findById(id).map(this::toResponse)
         .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
 }

 public List<SaleInvoiceResponse> getByBar(Long barId) {
     return invoiceRepository.findByBarId(barId).stream().map(this::toResponse).toList();
 }

 public List<SaleInvoiceResponse> getByBarAndDateRange(Long barId, LocalDateTime from, LocalDateTime to) {
     return invoiceRepository.findByBarIdAndSaleTimestampBetween(barId, from, to).stream().map(this::toResponse).toList();
 }

 private SaleInvoiceResponse toResponse(SaleInvoice inv) {
     List<SaleLineResponse> lines = inv.getLines().stream().map(l -> new SaleLineResponse(
         l.getId(), l.getDepotPackId(), l.getCachedBrandName(), l.getCachedSizeMl(), l.getCachedPackagingType(),
         l.getQuantity(), l.getSnapshotMrp(), l.getSnapshotSellingPrice(), l.getSnapshotPurchaseCost(),
         l.getLineTotal(), l.getLineProfit()
     )).toList();
     return new SaleInvoiceResponse(inv.getId(), inv.getBarId(), inv.getInvoiceNumber(), inv.getSaleTimestamp(),
         inv.getTotalAmount(), inv.getTotalProfit(), lines);
 }
}