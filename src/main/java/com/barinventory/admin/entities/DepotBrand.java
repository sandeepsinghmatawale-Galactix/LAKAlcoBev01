package com.barinventory.admin.entities;

import java.time.LocalDateTime;

import com.barinventory.admin.enums.EntityStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depot_brand")
@Getter
@Setter
public class DepotBrand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long brandId;

	@Column(nullable = false, unique = true)
	private String brandName;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private DepotCategory category;

	@ManyToOne
	@JoinColumn(name = "subcategory_id", nullable = false)
	private DepotSubCategory subCategory;

	@ManyToOne
	@JoinColumn(name = "manufacturer_id")
	private DepotManufacturer manufacturer;

	@ManyToOne
	@JoinColumn(name = "distributor_id")
	private DepotDistributor distributor;

	private String countryOfOrigin; // Indian Made / Imported
	private Double abv; // alcohol by volume %

	@Enumerated(EnumType.STRING)
	private EntityStatus status = EntityStatus.ACTIVE;

	private Boolean visibleToBars = true; // admin toggle - controls platform-wide visibility

	private String description;
	private String imageUrl; // brand logo/bottle image

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}