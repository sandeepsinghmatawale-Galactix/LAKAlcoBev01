package com.barinventory.admin.dtos;

//admin/dtos/PriceUpdateRequest.java

import java.time.LocalDateTime;

public record PriceUpdateRequest(Double mrp, Double exciseDuty, Double basePrice, LocalDateTime effectiveFrom,
		String revisionReason, String createdByAdminId) {
}