package com.barinventory.inventory.dtos;

//inventory/dtos/StockBatchResponse.java


import java.time.LocalDateTime;

public record StockBatchResponse(
 Long id, Long barId, Long depotPackId,
 Integer quantityReceived, Integer quantityRemaining,
 Double purchasePricePerUnit, String invoiceRefNo, LocalDateTime receivedAt
) {}
