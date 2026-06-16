package com.barinventory.inventory.dtos;

//inventory/dtos/reports/ReportFilterRequest.java
 

import java.time.LocalDateTime;

public record ReportFilterRequest(
 Long barId,
 LocalDateTime from,
 LocalDateTime to,
 Long sessionId   // optional - if provided, pulls single session report
) {}