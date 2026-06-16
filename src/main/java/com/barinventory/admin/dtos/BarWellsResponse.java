package com.barinventory.admin.dtos;

//admin/dtos/BarWellsResponse.java
 

import java.util.List;

public record BarWellsResponse(
 Long barId,
 String barName,
 List<WellDto> wells
) {
 public record WellDto(Long wellId, String wellName) {}
}