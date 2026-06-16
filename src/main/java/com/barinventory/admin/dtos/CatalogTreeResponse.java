package com.barinventory.admin.dtos;

//admin/dtos/CatalogTreeResponse.java

import java.util.List;

public record CatalogTreeResponse(Long brandId, String brandName, String categoryName, String subCategoryName,
		String manufacturerName, String distributorName, String status, Boolean visibleToBars, List<SizeNode> sizes) {
	public record SizeNode(Long brandSizeId, Integer sizeMl, String status, Boolean visibleToBars,
			List<PackNode> packs) {
	}

	public record PackNode(Long packId, String packagingType, Integer unitsPerCase, String status,
			Boolean visibleToBars, Double currentMrp) {
	}
}