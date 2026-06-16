package com.barinventory.inventory.dtos;
//inventory/dtos/reports/DailyInventoryReport.java
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DailyInventoryReport(
 Long barId,
 String barName,
 Long sessionId,
 String sessionName,
 LocalDateTime sessionDate,
 String sessionStatus,

 // Stockroom summary
 List<StockroomReportLine> stockroom,

 // Per-well breakdown
 List<WellReportLine> wells,

 // Sales of the session/day
 List<DailySalesSummary> sales,

 // Totals
 Double totalSaleAmount,
 Double totalProfit,
 Double totalPurchaseCost,
 Integer totalUnitsSold
) {}