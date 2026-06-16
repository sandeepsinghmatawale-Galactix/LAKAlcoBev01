package com.barinventory.inventory.dtos;


import lombok.Data;

@Data
public class StockroomInventoryView {
    private Long stockroomId;
    private Long depotBrandSizeId;
    private String cachedBrandName;
    private Integer cachedSizeMl;
    private Integer openingStock;
    private Integer receivedStock;
    private Integer closingStock;
    private Integer saleStock;
}