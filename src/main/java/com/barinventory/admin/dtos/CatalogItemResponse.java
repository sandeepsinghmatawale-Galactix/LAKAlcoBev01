package com.barinventory.admin.dtos;

public record CatalogItemResponse(Long packId, Long brandSizeId, Long brandId, String brandName, Integer sizeMl,
		String packagingType, String categoryName, String subCategoryName, Double mrp, Integer unitsPerCase) {
}