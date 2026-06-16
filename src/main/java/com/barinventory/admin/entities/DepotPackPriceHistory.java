package com.barinventory.admin.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depot_pack_price_history")
@Getter
@Setter
public class DepotPackPriceHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long priceId;

	@ManyToOne
	@JoinColumn(name = "pack_id", nullable = false)
	private DepotBrandSizePack pack;

	private Double mrp;
	private Double exciseDuty;
	private Double basePrice; // ex-distillery/depot price before excise

	@Column(nullable = false)
	private LocalDateTime effectiveFrom;

	private LocalDateTime effectiveTo; // null = currently active

	private String revisionReason; // "GO MS No. 45 dated ..."
	private String createdByAdminId;

	private LocalDateTime createdAt;
}