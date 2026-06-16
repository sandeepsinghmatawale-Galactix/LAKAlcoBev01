package com.barinventory.admin.dtos;
//admin/dtos/WellOpeningStockEntry.java
 

public record WellOpeningStockEntry(
 String wellName,       // matched to wells created in same request
 Long depotPackId,
 Long depotBrandSizeId,
 Integer openingQty
) {}