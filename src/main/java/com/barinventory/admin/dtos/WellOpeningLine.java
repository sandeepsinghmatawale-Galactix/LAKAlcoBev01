package com.barinventory.admin.dtos;

 
public record WellOpeningLine(
 Long wellId,
 Long depotBrandSizeId,
 Long depotPackId,
 Integer openingQty
) {}