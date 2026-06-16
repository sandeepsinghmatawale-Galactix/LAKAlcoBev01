package com.barinventory.inventory.dtos;

 

import java.time.LocalDateTime;
import java.util.List;

public record SaleInvoiceResponse(
 Long id, Long barId, String invoiceNumber, LocalDateTime saleTimestamp,
 Double totalAmount, Double totalProfit, List<SaleLineResponse> lines
) {}