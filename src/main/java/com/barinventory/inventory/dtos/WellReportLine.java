package com.barinventory.inventory.dtos;

//inventory/dtos/reports/WellReportLine.java
 

public record WellReportLine(
 Long wellId,
 String wellName,
 Long depotPackId,
 String brandName,
 Integer sizeMl,
 String packagingType,
 Integer openingStock,
 Integer receivedStock,
 Integer saleStock,
 Integer closingStock,
 Double sellingPrice,
 Double saleAmount,         // saleStock * sellingPrice
 Double purchaseCost,       // saleStock * snapshotPurchaseCost (from SaleInvoiceLine)
 Double profit              // saleAmount - purchaseCost
) {}