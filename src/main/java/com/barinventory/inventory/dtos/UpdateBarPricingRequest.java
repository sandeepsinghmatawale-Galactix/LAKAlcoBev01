package com.barinventory.inventory.dtos;

public record UpdateBarPricingRequest(Double sellingPrice, Double purchasePrice, Boolean priceLockedToMrp,
		String status) {
}