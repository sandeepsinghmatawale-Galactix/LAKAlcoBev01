package com.barinventory.inventory.entities;

//inventory/entities/StockBatch.java

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stock_batches")
@Data
public class StockBatch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bar_id", nullable = false)
	private Long barId;

	@Column(name = "depot_pack_id", nullable = false)
	private Long depotPackId;

	@Column(nullable = false)
	private Integer quantityReceived;

	@Column(nullable = false)
	private Integer quantityRemaining; // decremented as sales consume this batch (FIFO)

	@Column(nullable = false)
	private Double purchasePricePerUnit; // actual cost paid for this batch

	private String invoiceRefNo; // distributor/depot invoice reference

	@Column(nullable = false)
	private LocalDateTime receivedAt;

	private LocalDateTime createdAt;
}