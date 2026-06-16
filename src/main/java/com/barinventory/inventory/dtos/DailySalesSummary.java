package com.barinventory.inventory.dtos;
//inventory/dtos/reports/DailySalesSummary.java
 

public record DailySalesSummary(
 Long invoiceId,
 String invoiceNumber,
 java.time.LocalDateTime saleTimestamp,
 Double totalAmount,
 Double totalProfit
) {}