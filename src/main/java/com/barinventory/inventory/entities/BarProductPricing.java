package com.barinventory.inventory.entities;

//inventory/entities/BarProductPricing.java
 

import java.time.LocalDateTime;

import com.barinventory.admin.enums.BarPricingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "bar_product_pricings", uniqueConstraints = @UniqueConstraint(columnNames = { "bar_id", "depot_pack_id" }))
@Data
public class BarProductPricing {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "bar_id", nullable = false)
 private Long barId;

 @Column(name = "depot_brand_id", nullable = false)
 private Long depotBrandId;

 @Column(name = "depot_brand_size_id", nullable = false)
 private Long depotBrandSizeId;

 @Column(name = "depot_pack_id", nullable = false)
 private Long depotPackId;

 // Denormalized cache - refreshed on PackPriceChangedEvent
 private String cachedBrandName;
 private Integer cachedSizeMl;
 private String cachedPackagingType;
 private Double cachedMrp;

 private Double purchasePrice; // bar's weighted avg purchase cost
 private Double sellingPrice;  // bar's actual selling price

 @Column(nullable = false)
 private Boolean priceLockedToMrp = true; // if true, sellingPrice auto-tracks cachedMrp on govt revision

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private BarPricingStatus status = BarPricingStatus.ACTIVE; // bar-level visibility toggle

 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
}