package com.barinventory.admin.dtos;
//admin/dtos/EditSubscriptionRequest.java
 

import java.time.LocalDateTime;

public record EditSubscriptionRequest(
 String status,
 String trialType,
 LocalDateTime startDate,
 LocalDateTime endDate
) {}