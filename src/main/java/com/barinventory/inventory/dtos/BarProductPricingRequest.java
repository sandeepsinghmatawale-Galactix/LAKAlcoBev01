package com.barinventory.inventory.dtos;

//inventory/dtos/BarProductPricingRequest.java

public record BarProductPricingRequest(Long barId, Long depotPackId, Double sellingPrice, // optional - defaults to
																							// current MRP if null
		Double purchasePrice // optional - set on first stock receipt typically
) {
}