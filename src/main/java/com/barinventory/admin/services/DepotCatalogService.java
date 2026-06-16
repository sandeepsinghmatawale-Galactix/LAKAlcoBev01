package com.barinventory.admin.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.CatalogItemResponse;
import com.barinventory.admin.dtos.CatalogTreeResponse;
import com.barinventory.admin.entities.DepotBrand;
import com.barinventory.admin.entities.DepotBrandSize;

//admin/services/DepotCatalogService.java

import com.barinventory.admin.entities.DepotBrandSizePack;
import com.barinventory.admin.entities.DepotPackPriceHistory;
import com.barinventory.admin.enums.EntityStatus;
import com.barinventory.admin.repsitory.DepotBrandRepository;
import com.barinventory.admin.repsitory.DepotBrandSizePackRepository;
import com.barinventory.admin.repsitory.DepotBrandSizeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotCatalogService {
	private static final List<EntityStatus> SELLABLE_STATUSES = List.of(EntityStatus.ACTIVE, EntityStatus.NEW_LAUNCH);

	private final DepotBrandRepository brandRepository;
	private final DepotBrandSizeRepository sizeRepository;
	private final DepotBrandSizePackRepository packRepository;
	private final DepotPackPriceHistoryService priceHistoryService;

	/** Full nested tree for admin catalog management UI. */
	public List<CatalogTreeResponse> getFullCatalogTree() {
		return brandRepository.findAll().stream().map(brand -> {
			List<CatalogTreeResponse.SizeNode> sizeNodes = sizeRepository.findByBrand_BrandId(brand.getBrandId())
					.stream().map(this::toSizeNode).toList();
			return toBrandNode(brand, sizeNodes);
		}).toList();
	}

	public CatalogTreeResponse getBrandTree(Long brandId) {
		DepotBrand brand = brandRepository.findById(brandId).orElseThrow(
				() -> new com.barinventory.admin.exceptions.ResourceNotFoundException("Brand not found: " + brandId));
		List<CatalogTreeResponse.SizeNode> sizeNodes = sizeRepository.findByBrand_BrandId(brandId).stream()
				.map(this::toSizeNode).toList();
		return toBrandNode(brand, sizeNodes);
	}

	/**
	 * Flat, bar-facing catalog: only active + visible items, current price
	 * resolved.
	 */
	public List<CatalogItemResponse> getBarCatalog() {
		return packRepository.findByVisibleToBarsTrueAndStatusIn(SELLABLE_STATUSES).stream().filter(pack -> {
			DepotBrandSize size = pack.getBrandSize();
			DepotBrand brand = size.getBrand();
			return size.getVisibleToBars() && SELLABLE_STATUSES.contains(size.getStatus()) && brand.getVisibleToBars()
					&& SELLABLE_STATUSES.contains(brand.getStatus());
		}).map(this::toCatalogItem).toList();
	}

	private CatalogTreeResponse.SizeNode toSizeNode(DepotBrandSize size) {
		List<CatalogTreeResponse.PackNode> packNodes = packRepository.findByBrandSize_BrandSizeId(size.getBrandSizeId())
				.stream()
				.map(pack -> new CatalogTreeResponse.PackNode(pack.getPackId(), pack.getPackagingType().name(),
						pack.getUnitsPerCase(), pack.getStatus().name(), pack.getVisibleToBars(), priceHistoryService
								.getCurrentPrice(pack.getPackId()).map(DepotPackPriceHistory::getMrp).orElse(null)))
				.toList();
		return new CatalogTreeResponse.SizeNode(size.getBrandSizeId(), size.getSizeMl(), size.getStatus().name(),
				size.getVisibleToBars(), packNodes);
	}

	private CatalogTreeResponse toBrandNode(DepotBrand brand, List<CatalogTreeResponse.SizeNode> sizeNodes) {
		return new CatalogTreeResponse(brand.getBrandId(), brand.getBrandName(), brand.getCategory().getCategoryName(),
				brand.getSubCategory().getSubCategoryName(),
				brand.getManufacturer() != null ? brand.getManufacturer().getManufacturerName() : null,
				brand.getDistributor() != null ? brand.getDistributor().getDistributorName() : null,
				brand.getStatus().name(), brand.getVisibleToBars(), sizeNodes);
	}

	private CatalogItemResponse toCatalogItem(DepotBrandSizePack pack) {
		DepotBrandSize size = pack.getBrandSize();
		DepotBrand brand = size.getBrand();
		return new CatalogItemResponse(pack.getPackId(), size.getBrandSizeId(), brand.getBrandId(),
				brand.getBrandName(), size.getSizeMl(), pack.getPackagingType().name(),
				brand.getCategory().getCategoryName(), brand.getSubCategory().getSubCategoryName(),
				priceHistoryService.getCurrentPrice(pack.getPackId()).map(DepotPackPriceHistory::getMrp).orElse(null),
				pack.getUnitsPerCase());
	}

//admin/services/DepotCatalogService.java (add this method to the existing class)

	public CatalogItemResponse getCatalogItemByPackId(Long packId) {
		DepotBrandSizePack pack = packRepository.findById(packId).orElseThrow(
				() -> new com.barinventory.admin.exceptions.ResourceNotFoundException("Pack not found: " + packId));
		return toCatalogItem(pack);
	}

}