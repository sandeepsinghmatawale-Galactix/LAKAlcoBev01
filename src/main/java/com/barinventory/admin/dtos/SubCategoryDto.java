package com.barinventory.admin.dtos;
//admin/dtos/SubCategoryDto.java

public record SubCategoryDto(Long subCategoryId, Long categoryId, String categoryName, String subCategoryName) {
}