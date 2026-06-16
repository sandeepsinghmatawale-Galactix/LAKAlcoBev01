package com.barinventory.admin.dtos;

//admin/dtos/DepotBrandSizePackRequest.java

public record DepotBrandSizePackRequest(String packagingType, Integer unitsPerCase, String barcode, String hsnCode) {
}