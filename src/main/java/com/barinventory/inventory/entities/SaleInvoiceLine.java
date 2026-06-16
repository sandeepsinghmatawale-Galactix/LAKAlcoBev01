package com.barinventory.inventory.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

 

@Entity(name = "InventorySaleInvoiceLine")
@Table(name = "inventory_sale_invoice_lines")
@Getter
@Setter
public class SaleInvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private SaleInvoice invoice;

    @Column(name = "depot_pack_id", nullable = false)
    private Long depotPackId;

    @Column(nullable = false)
    private Integer quantity;

    private Double snapshotMrp;
    private Double snapshotSellingPrice;
    private Double snapshotPurchaseCost;

    private Double lineTotal;
    private Double lineProfit;

    private String cachedBrandName;
    private Integer cachedSizeMl;
    private String cachedPackagingType;

    // Explicit setter - Lombok not processing correctly in this IDE
    public void setInvoice(SaleInvoice invoice) {
        this.invoice = invoice;
    }
}