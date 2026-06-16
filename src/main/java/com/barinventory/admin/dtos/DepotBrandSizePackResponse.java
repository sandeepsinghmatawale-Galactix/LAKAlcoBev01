package com.barinventory.admin.dtos;

//admin/dtos/DepotBrandSizePackResponse.java

import java.time.LocalDateTime;

public record DepotBrandSizePackResponse(Long packId, Long brandSizeId, String brandName, Integer sizeMl,
		String packagingType, Integer unitsPerCase, String barcode, String hsnCode, String status,
		Boolean visibleToBars, Double currentMrp, LocalDateTime priceEffectiveFrom) {
}