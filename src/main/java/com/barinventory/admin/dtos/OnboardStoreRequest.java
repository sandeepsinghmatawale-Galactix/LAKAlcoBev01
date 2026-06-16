package com.barinventory.admin.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.barinventory.subscriptions.enums.TrialType;

import lombok.Data;
 

 

@Data
public class OnboardStoreRequest {
 // Bar fields
 private String storeName;
 private String ownerName;
 private String phone;
 private String email;
 private String licenseNumber;
 private String address;
 private String city;
 private String state;
 private String pincode;

 // Auth
 private String adminUsername;
 private String adminPassword;

 // Subscription
 private TrialType trialType;
 private LocalDateTime customStartDate;
 private LocalDateTime customEndDate;

 // Wells to create for this bar
 private List<String> wellNames;

 // Opening stock for stockroom
 private List<OpeningStockEntry> stockroomOpeningStock;

 // Opening stock per well
 private List<WellOpeningStockEntry> wellOpeningStock;
 
//OnboardStoreRequest.java - add this field + getter
private String wellNamesRaw;

public List<String> getWellNames() {
  if (wellNamesRaw == null || wellNamesRaw.isBlank()) return List.of();
  return List.of(wellNamesRaw.split(",")).stream().map(String::trim).filter(s -> !s.isBlank()).toList();
}
}