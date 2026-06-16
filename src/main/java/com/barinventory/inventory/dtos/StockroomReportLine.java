package com.barinventory.inventory.dtos;

//inventory/dtos/reports/StockroomReportLine.java
 

public record StockroomReportLine(
 Long depotBrandSizeId,
 Long depotPackId,
 String brandName,
 Integer sizeMl,
 String packagingType,
 Integer openingStock,
 Integer receivedStock,
 Integer distributedToWells,
 Integer closingStock,
 Double purchaseCost,       // avg unit cost * closing stock
 Double currentMrp
) {}