package com.barinventory.admin.entities;

import java.time.LocalDateTime;

import com.barinventory.admin.enums.EntityStatus;
import com.barinventory.admin.enums.PackagingType;

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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depot_brand_size_pack", uniqueConstraints = @UniqueConstraint(columnNames = { "brand_size_id",
		"packaging_type" }))
@Getter
@Setter
public class DepotBrandSizePack {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long packId;

	@ManyToOne
	@JoinColumn(name = "brand_size_id", nullable = false)
	private DepotBrandSize brandSize;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PackagingType packagingType;

	private Integer unitsPerCase; // case size differs glass vs PET

	private String barcode;
	private String hsnCode; // for GST/tax

	@Enumerated(EnumType.STRING)
	private EntityStatus status = EntityStatus.ACTIVE;

	private Boolean visibleToBars = true;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}