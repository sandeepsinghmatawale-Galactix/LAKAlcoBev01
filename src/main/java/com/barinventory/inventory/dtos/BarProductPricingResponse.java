package com.barinventory.inventory.dtos;

//inventory/dtos/BarProductPricingResponse.java

public record BarProductPricingResponse(
 Long id,
 Long barId,
 Long depotBrandId,
 Long depotBrandSizeId,
 Long depotPackId,
 String cachedBrandName,
 Integer cachedSizeMl,
 String cachedPackagingType,
 Double cachedMrp,
 Double purchasePrice,
 Double sellingPrice,
 Boolean priceLockedToMrp,
 String status
) {}