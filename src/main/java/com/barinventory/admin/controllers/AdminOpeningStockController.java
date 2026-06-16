package com.barinventory.admin.controllers;

//admin/controllers/AdminOpeningStockController.java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barinventory.admin.dtos.AdminOpeningStockRequest;
import com.barinventory.admin.dtos.AdminOpeningStockResponse;
import com.barinventory.admin.dtos.BarWellsResponse;
import com.barinventory.admin.services.AdminOpeningStockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/opening-stock")
@RequiredArgsConstructor
public class AdminOpeningStockController {
 private final AdminOpeningStockService openingStockService;

 /** Step 1: Admin fetches wells for bar to populate the UI form */
 @GetMapping("/bars/{barId}/wells")
 public BarWellsResponse getBarWells(@PathVariable Long barId) {
     return openingStockService.getBarWells(barId);
 }

 /** Step 2: Admin submits full opening stock for stockroom + all wells */
 @PostMapping
 public AdminOpeningStockResponse seedOpeningStock(
     @RequestBody AdminOpeningStockRequest req) {
     return openingStockService.seedOpeningStock(req);
 }
}