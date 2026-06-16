package com.barinventory.inventory.dtos;
//inventory/dtos/reports/BarReportSummary.java
 
import java.time.LocalDateTime;

public record BarReportSummary(
 Long sessionId,
 String sessionName,
 LocalDateTime sessionDate,
 String sessionStatus,
 Double totalSaleAmount,
 Double totalProfit,
 Integer totalUnitsSold
) {}