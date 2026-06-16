package com.barinventory.admin.dtos;

public record DepotBrandResponse(Long brandId, String brandName, Long categoryId, String categoryName,
		Long subCategoryId, String subCategoryName, Long manufacturerId, String manufacturerName, Long distributorId,
		String distributorName, String countryOfOrigin, Double abv, String status, Boolean visibleToBars,
		String description, String imageUrl) {
}