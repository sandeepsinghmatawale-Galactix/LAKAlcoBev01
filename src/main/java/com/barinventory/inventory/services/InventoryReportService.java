package com.barinventory.inventory.services;

//inventory/services/InventoryReportService.java
 

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.barinventory.admin.repsitory.DepotBrandSizePackRepository;
import com.barinventory.admin.services.DepotPackPriceHistoryService;
import com.barinventory.auth.entities.Bar;
import com.barinventory.auth.repos.BarRepository;
import com.barinventory.inventory.dtos.BarReportSummary;
import com.barinventory.inventory.dtos.DailyInventoryReport;
import com.barinventory.inventory.dtos.DailySalesSummary;
import com.barinventory.inventory.dtos.StockroomReportLine;
import com.barinventory.inventory.dtos.WellReportLine;
import com.barinventory.inventory.entities.BarProductPricing;
import com.barinventory.inventory.entities.InventorySession;
import com.barinventory.inventory.entities.SaleInvoice;
import com.barinventory.inventory.entities.SaleInvoiceLine;
import com.barinventory.inventory.entities.SessionStatus;
import com.barinventory.inventory.entities.StockroomInventory;
import com.barinventory.inventory.entities.WellInventory;
import com.barinventory.inventory.exceptions.ResourceNotFoundException;
import com.barinventory.inventory.repos.BarProductPricingRepository;
import com.barinventory.inventory.repos.InventorySessionRepository;
import com.barinventory.inventory.repos.SaleInvoiceRepository;
import com.barinventory.inventory.repos.StockBatchRepository;
import com.barinventory.inventory.repos.StockroomInventoryRepository;
import com.barinventory.inventory.repos.WellInventoryRepository;

import lombok.RequiredArgsConstructor;
 

@Service
@RequiredArgsConstructor
public class InventoryReportService {

 private final InventorySessionRepository sessionRepository;
 private final StockroomInventoryRepository stockroomRepository;
 private final WellInventoryRepository wellInventoryRepository;
 private final SaleInvoiceRepository invoiceRepository;
 private final StockBatchRepository batchRepository;
 private final BarProductPricingRepository pricingRepository;
 private final BarRepository barRepository;
 private final DepotBrandSizePackRepository packRepository;
 private final DepotPackPriceHistoryService priceHistoryService;

 /** Single session full report */
 public DailyInventoryReport getSessionReport(Long sessionId) {
     InventorySession session = sessionRepository.findById(sessionId)
         .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
     return buildReport(session);
 }

 /** All sessions for a bar filtered by date range */
 public List<BarReportSummary> getBarReportSummaries(Long barId, LocalDateTime from, LocalDateTime to) {
     List<InventorySession> sessions = (from != null && to != null)
         ? sessionRepository.findByBarBarIdAndSessionDateBetweenOrderBySessionDateDesc(barId, from, to)
         : sessionRepository.findByBarBarId(barId);

     return sessions.stream().map(session -> {
         List<SaleInvoice> invoices = invoiceRepository.findByBarIdAndSaleTimestampBetween(
             barId,
             session.getSessionDate(),
             session.getSessionDate().plusDays(1)
         );
         double totalSale = invoices.stream().mapToDouble(i -> i.getTotalAmount() != null ? i.getTotalAmount() : 0).sum();
         double totalProfit = invoices.stream().mapToDouble(i -> i.getTotalProfit() != null ? i.getTotalProfit() : 0).sum();
         int totalUnits = invoices.stream()
             .flatMap(i -> i.getLines().stream())
             .mapToInt(SaleInvoiceLine::getQuantity).sum();

         return new BarReportSummary(
             session.getSessionId(), session.getSessionName(),
             session.getSessionDate(), session.getStatus().name(),
             totalSale, totalProfit, totalUnits
         );
     }).toList();
 }

 /** Active session quick report for bar dashboard */
 public DailyInventoryReport getActiveSessionReport(Long barId) {
     InventorySession session = sessionRepository.findByBarBarIdAndStatus(barId, SessionStatus.OPEN)
         .orElseThrow(() -> new ResourceNotFoundException("No active session for bar: " + barId));
     return buildReport(session);
 }

 private DailyInventoryReport buildReport(InventorySession session) {
     Long barId = session.getBar().getBarId();
     Bar bar = barRepository.findById(barId)
         .orElseThrow(() -> new ResourceNotFoundException("Bar not found: " + barId));

     // --- Stockroom lines ---
     List<StockroomInventory> stockrooms = stockroomRepository.findBySessionSessionId(session.getSessionId());
     List<StockroomReportLine> stockroomLines = buildStockroomLines(barId, stockrooms);

     // --- Well lines ---
     List<WellInventory> wellInventories = wellInventoryRepository.findByBarId(barId)
         .stream()
         .filter(wi -> wi.getSession() != null &&
             wi.getSession().getSessionId().equals(session.getSessionId()))
         .toList();
     List<WellReportLine> wellLines = buildWellLines(wellInventories, barId, session);

     // --- Sales in this session window ---
     LocalDateTime sessionEnd = session.getStatus() == SessionStatus.OPEN
         ? LocalDateTime.now()
         : session.getSessionDate().plusDays(1);

     List<SaleInvoice> invoices = invoiceRepository.findByBarIdAndSaleTimestampBetween(
         barId, session.getSessionDate(), sessionEnd);

     List<DailySalesSummary> salesSummaries = invoices.stream().map(inv ->
         new DailySalesSummary(inv.getId(), inv.getInvoiceNumber(),
             inv.getSaleTimestamp(), inv.getTotalAmount(), inv.getTotalProfit())
     ).toList();

     double totalSaleAmount = invoices.stream()
         .mapToDouble(i -> i.getTotalAmount() != null ? i.getTotalAmount() : 0).sum();
     double totalProfit = invoices.stream()
         .mapToDouble(i -> i.getTotalProfit() != null ? i.getTotalProfit() : 0).sum();
     double totalPurchaseCost = invoices.stream()
         .flatMap(i -> i.getLines().stream())
         .mapToDouble(l -> l.getSnapshotPurchaseCost() != null && l.getQuantity() != null
             ? l.getSnapshotPurchaseCost() * l.getQuantity() : 0).sum();
     int totalUnitsSold = invoices.stream()
         .flatMap(i -> i.getLines().stream())
         .mapToInt(SaleInvoiceLine::getQuantity).sum();

     return new DailyInventoryReport(
         barId, bar.getBarName(),
         session.getSessionId(), session.getSessionName(),
         session.getSessionDate(), session.getStatus().name(),
         stockroomLines, wellLines, salesSummaries,
         totalSaleAmount, totalProfit, totalPurchaseCost, totalUnitsSold
     );
 }

 private List<StockroomReportLine> buildStockroomLines(Long barId, List<StockroomInventory> stockrooms) {
     List<StockroomReportLine> lines = new ArrayList<>();
     Map<Long, BarProductPricing> pricingMap = pricingRepository.findByBarId(barId)
         .stream().collect(Collectors.toMap(BarProductPricing::getDepotBrandSizeId, p -> p, (a, b) -> a));

     for (StockroomInventory s : stockrooms) {
         BarProductPricing pricing = pricingMap.get(s.getDepotBrandSizeId());
         if (pricing == null) continue;

         Integer onHand = batchRepository.getStockOnHand(barId, pricing.getDepotPackId());
         Double currentMrp = priceHistoryService.getCurrentPrice(pricing.getDepotPackId())
             .map(p -> p.getMrp()).orElse(null);

         lines.add(new StockroomReportLine(
             s.getDepotBrandSizeId(),
             pricing.getDepotPackId(),
             pricing.getCachedBrandName(),
             pricing.getCachedSizeMl(),
             pricing.getCachedPackagingType(),
             s.getOpeningStock(),
             s.getReceivedStock() != null ? s.getReceivedStock() : 0,
             0, // distributedToWells - extend later when distribution tracking added
             s.getClosingStock() != null ? s.getClosingStock() : 0,
             onHand != null && pricing.getPurchasePrice() != null
                 ? onHand * pricing.getPurchasePrice() : 0.0,
             currentMrp
         ));
     }
     return lines;
 }

 private List<WellReportLine> buildWellLines(List<WellInventory> wellInventories,
                                              Long barId, InventorySession session) {
     List<WellReportLine> lines = new ArrayList<>();

     // Sales lines grouped by packId for cost lookup
     LocalDateTime sessionEnd = session.getStatus() == SessionStatus.OPEN
         ? LocalDateTime.now() : session.getSessionDate().plusDays(1);

     Map<Long, Double> packIdToAvgCost = invoiceRepository
         .findByBarIdAndSaleTimestampBetween(barId, session.getSessionDate(), sessionEnd)
         .stream()
         .flatMap(i -> i.getLines().stream())
         .collect(Collectors.groupingBy(
             SaleInvoiceLine::getDepotPackId,
             Collectors.averagingDouble(l -> l.getSnapshotPurchaseCost() != null
                 ? l.getSnapshotPurchaseCost() : 0.0)
         ));

     for (WellInventory wi : wellInventories) {
         BarProductPricing pricing = wi.getProductPricing();
         if (pricing == null) continue;

         int saleStock = wi.getSaleStock() != null ? wi.getSaleStock() : 0;
         double sellingPrice = pricing.getSellingPrice() != null ? pricing.getSellingPrice() : 0.0;
         double saleAmount = saleStock * sellingPrice;
         double avgCost = packIdToAvgCost.getOrDefault(pricing.getDepotPackId(), 0.0);
         double purchaseCost = saleStock * avgCost;
         double profit = saleAmount - purchaseCost;

         lines.add(new WellReportLine(
             wi.getWell().getWellId(),
             wi.getWell().getWellName(),
             pricing.getDepotPackId(),
             pricing.getCachedBrandName(),
             pricing.getCachedSizeMl(),
             pricing.getCachedPackagingType(),
             wi.getOpeningStock() != null ? wi.getOpeningStock() : 0,
             wi.getReceivedStock() != null ? wi.getReceivedStock() : 0,
             saleStock,
             wi.getClosingStock() != null ? wi.getClosingStock() : 0,
             sellingPrice, saleAmount, purchaseCost, profit
         ));
     }
     return lines;
 }
}