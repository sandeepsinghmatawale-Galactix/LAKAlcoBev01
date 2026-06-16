package com.barinventory.admin.dtos;

//admin/dtos/AdminOpeningStockResponse.java
 

import java.util.List;

public record AdminOpeningStockResponse(
 Long barId,
 String barName,
 int stockroomLinesCreated,
 int wellLinesCreated,
 List<WellSummary> wellSummaries
) {
 public record WellSummary(Long wellId, String wellName, int itemsSeeded) {}
}