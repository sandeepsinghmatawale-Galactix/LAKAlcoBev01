package com.barinventory.admin.dtos;

//admin/dtos/EditBarRequest.java
 

public record EditBarRequest(
 String barName,
 String ownerName,
 String phone,
 String email,
 String licenseNumber,
 String address,
 String city,
 String state,
 String pincode,
 String status
) {}