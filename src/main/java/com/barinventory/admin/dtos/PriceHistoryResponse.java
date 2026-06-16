package com.barinventory.admin.dtos;

//admin/dtos/PriceHistoryResponse.java

import java.time.LocalDateTime;

public record PriceHistoryResponse(Long priceId, Long packId, Double mrp, Double exciseDuty, Double basePrice,
		LocalDateTime effectiveFrom, LocalDateTime effectiveTo, String revisionReason) {
}