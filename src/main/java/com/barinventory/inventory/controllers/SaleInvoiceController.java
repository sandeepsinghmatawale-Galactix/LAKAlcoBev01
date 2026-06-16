package com.barinventory.inventory.controllers;

//inventory/controllers/SaleInvoiceController.java
 

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barinventory.inventory.dtos.SaleInvoiceRequest;
import com.barinventory.inventory.dtos.SaleInvoiceResponse;
import com.barinventory.inventory.services.SaleInvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory/sales")
@RequiredArgsConstructor
public class SaleInvoiceController {
 private final SaleInvoiceService saleService;

 @PostMapping
 public SaleInvoiceResponse createSale(@RequestBody SaleInvoiceRequest req) { return saleService.createSale(req); }

 @GetMapping("/{id}")
 public SaleInvoiceResponse getById(@PathVariable Long id) { return saleService.getById(id); }

 @GetMapping
 public List<SaleInvoiceResponse> getByBar(
     @RequestParam Long barId,
     @RequestParam(required = false) LocalDateTime from,
     @RequestParam(required = false) LocalDateTime to
 ) {
     if (from != null && to != null) return saleService.getByBarAndDateRange(barId, from, to);
     return saleService.getByBar(barId);
 }
}