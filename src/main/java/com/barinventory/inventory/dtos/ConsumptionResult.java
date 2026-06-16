package com.barinventory.inventory.dtos;

//inventory/dtos/ConsumptionResult.java


public record ConsumptionResult(
 Integer quantityConsumed,
 Double totalCost,
 Double avgUnitCost
) {}
