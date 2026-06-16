package com.barinventory.admin.controllers;
//inventory/controllers/InventoryCatalogController.java
 

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barinventory.admin.dtos.CatalogItemResponse;
import com.barinventory.admin.services.DepotCatalogService;

import lombok.RequiredArgsConstructor;

/**
 * Read-only catalog feed for bars: active + visible SKUs with current MRP, used
 * to set up BarProductPricing.
 */
@RestController
@RequestMapping("/inventory/catalog")
@RequiredArgsConstructor
public class InventoryCatalogController {
	private final DepotCatalogService catalogService;

	@GetMapping
	public List<CatalogItemResponse> getCatalog() {
		return catalogService.getBarCatalog();
	}
}