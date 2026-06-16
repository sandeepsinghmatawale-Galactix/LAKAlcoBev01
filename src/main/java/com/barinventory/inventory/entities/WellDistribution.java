package com.barinventory.inventory.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="well_distribution")
@Getter
@Setter
public class WellDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wellDistributionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="distribution_id")
    private Distribution distribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="well_id")
    private Well well; // Safe: maps to a Well configuration within your inventory layout



    
    @Column(name = "depot_brand_id", nullable = false)
    private Long depotBrandId;  // ✅ renamed from brandId

    @Column(name = "depot_brand_size_id", nullable = false)
    private Long depotBrandSizeId;  // ✅ renamed from brandSizeId
    
    private Integer distributedQty;
    private LocalDateTime distributedAt;
    
    @Version
    private Long version;
}