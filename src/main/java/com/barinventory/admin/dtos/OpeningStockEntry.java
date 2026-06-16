package com.barinventory.admin.dtos;

//admin/dtos/OpeningStockEntry.java
 

public record OpeningStockEntry(
 Long depotPackId,
 Long depotBrandSizeId,
 Integer quantity,
 Double purchasePricePerUnit,
 String invoiceRefNo
) {}