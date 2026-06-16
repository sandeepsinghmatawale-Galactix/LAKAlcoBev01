package com.barinventory.inventory.dtos;

//inventory/dtos/StockReceiptRequest.java


import java.time.LocalDateTime;

public record StockReceiptRequest(
 Long barId,
 Long depotPackId,
 Integer quantity,
 Double purchasePricePerUnit,
 String invoiceRefNo,
 LocalDateTime receivedAt
) {}