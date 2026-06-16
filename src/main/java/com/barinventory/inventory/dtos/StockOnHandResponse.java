package com.barinventory.inventory.dtos;

//inventory/dtos/StockOnHandResponse.java
 

public record StockOnHandResponse(Long barId, Long depotPackId, Integer quantityOnHand) {}