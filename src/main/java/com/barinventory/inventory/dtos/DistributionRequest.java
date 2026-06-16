package com.barinventory.inventory.dtos;

import lombok.Data;

@Data
public class DistributionRequest {

    private Long wellId;
    
    private Long depotBrandId; 
    private Integer distributedQty;
 
    
    private Long depotBrandSizeId;
}