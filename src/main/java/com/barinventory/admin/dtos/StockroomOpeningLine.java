package com.barinventory.admin.dtos;

//admin/dtos/StockroomOpeningLine.java
 

public record StockroomOpeningLine(
 Long depotBrandSizeId,
 Long depotPackId,
 Integer openingQty,
 Double purchasePricePerUnit,
 String invoiceRefNo,
 // Cache fields for BarProductPricing
 String brandName,
 Integer sizeMl,
 String packagingType,
 Double mrp,
 Double sellingPrice
) {}