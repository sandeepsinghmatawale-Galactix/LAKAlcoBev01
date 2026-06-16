package com.barinventory.inventory.dtos;

 

import java.time.LocalDateTime;
import java.util.List;

public record SaleInvoiceRequest(
 Long barId,
 String invoiceNumber,
 LocalDateTime saleTimestamp,
 List<SaleLineRequest> lines
) {}