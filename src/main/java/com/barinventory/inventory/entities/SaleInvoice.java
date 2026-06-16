package com.barinventory.inventory.entities; 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "sale_invoices")
@Getter
@Setter
public class SaleInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bar_id", nullable = false)
    private Long barId;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDateTime saleTimestamp;

    private Double totalAmount;
    private Double totalProfit;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleInvoiceLine> lines = new ArrayList<>();

    public void addLine(SaleInvoiceLine line) {
        line.setInvoice(this);
        this.lines.add(line);
    }

    private LocalDateTime createdAt;
}