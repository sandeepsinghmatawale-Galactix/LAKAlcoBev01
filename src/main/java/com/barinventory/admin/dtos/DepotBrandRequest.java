package com.barinventory.admin.dtos;

//admin/dtos/DepotBrandRequest.java

public record DepotBrandRequest(String brandName, Long categoryId, Long subCategoryId, Long manufacturerId,
		Long distributorId, String countryOfOrigin, Double abv, String description, String imageUrl) {
}