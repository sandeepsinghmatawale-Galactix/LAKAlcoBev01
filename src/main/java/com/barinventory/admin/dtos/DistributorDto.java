package com.barinventory.admin.dtos;

//admin/dtos/DistributorDto.java

public record DistributorDto(Long distributorId, String distributorName, String licenseNo, String contactInfo,
		String region) {
}