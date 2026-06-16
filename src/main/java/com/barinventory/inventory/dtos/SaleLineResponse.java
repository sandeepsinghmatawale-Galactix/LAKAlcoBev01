package com.barinventory.inventory.dtos;

//inventory/dtos/SaleLineResponse.java


public record SaleLineResponse(
 Long id, Long depotPackId, String cachedBrandName, Integer cachedSizeMl, String cachedPackagingType,
 Integer quantity, Double snapshotMrp, Double snapshotSellingPrice, Double snapshotPurchaseCost,
 Double lineTotal, Double lineProfit
) {}
