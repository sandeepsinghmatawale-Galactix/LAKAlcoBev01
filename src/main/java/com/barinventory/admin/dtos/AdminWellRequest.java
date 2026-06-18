package com.barinventory.admin.dtos;
 

public record AdminWellRequest(
        Long barId,
        String wellName
) {
}