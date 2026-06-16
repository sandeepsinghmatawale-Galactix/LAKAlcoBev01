package com.barinventory.inventory.controllers;
//inventory/controllers/BarReportPageController.java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.barinventory.auth.entities.Bar;
import com.barinventory.auth.repos.BarRepository;
import com.barinventory.inventory.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BarReportPageController {
 private final BarRepository barRepository;

 @GetMapping("/bar/{barId}/reports")
 public String reportsPage(@PathVariable Long barId, Model model) {
     Bar bar = barRepository.findById(barId)
         .orElseThrow(() -> new ResourceNotFoundException("Bar not found: " + barId));
     model.addAttribute("barId", barId);
     model.addAttribute("barName", bar.getBarName());
     return "bar-report";
 }
}