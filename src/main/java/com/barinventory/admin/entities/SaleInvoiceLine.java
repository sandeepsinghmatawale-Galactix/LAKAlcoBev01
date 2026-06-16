package com.barinventory.admin.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "AdminSaleInvoiceLine")
@Table(name = "admin_sale_invoice_lines")
@Getter
@Setter
public class SaleInvoiceLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long invoiceId;
	private Long depotPackId;

	private Integer quantity;

	// SNAPSHOTS — frozen at sale time, immune to later price changes
	private Double snapshotMrp;
	private Double snapshotSellingPrice;
	private Double snapshotPurchaseCost; // weighted avg cost of stock at sale time

	private Double lineTotal;
	private Double lineProfit; // (sellingPrice - purchaseCost) * qty, computed once and stored
}