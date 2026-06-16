package com.barinventory.admin.dtos;
//admin/dtos/DepotBrandSizeResponse.java

import java.time.LocalDateTime;

public record DepotBrandSizeResponse(Long brandSizeId, Long brandId, String brandName, Integer sizeMl, String status,
		Boolean visibleToBars, LocalDateTime launchDate, LocalDateTime discontinuedDate) {
}