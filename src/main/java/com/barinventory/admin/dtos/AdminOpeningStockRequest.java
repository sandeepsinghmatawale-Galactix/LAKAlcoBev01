package com.barinventory.admin.dtos;
//admin/dtos/AdminOpeningStockRequest.java
 

import java.util.List;

public record AdminOpeningStockRequest(
 Long barId,
 List<StockroomOpeningLine> stockroomLines,
 List<WellOpeningLine> wellLines
) {}