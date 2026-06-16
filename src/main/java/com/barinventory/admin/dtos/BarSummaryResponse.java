package com.barinventory.admin.dtos;

 

import java.time.LocalDateTime;

public record BarSummaryResponse(
 Long barId,
 String barName,
 String ownerName,
 String phone,
 String email,
 String licenseNumber,
 String city,
 String state,
 String status,
 String subscriptionStatus,
 LocalDateTime subscriptionEnd
) {}