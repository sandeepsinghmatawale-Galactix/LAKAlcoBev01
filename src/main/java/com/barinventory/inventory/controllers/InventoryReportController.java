package com.barinventory.inventory.controllers; 

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.barinventory.inventory.dtos.BarReportSummary;
import com.barinventory.inventory.dtos.DailyInventoryReport;
import com.barinventory.inventory.services.InventoryReportService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory/reports")
@RequiredArgsConstructor
public class InventoryReportController {
 private final InventoryReportService reportService;

 /** Full report for a specific session */
 @GetMapping("/session/{sessionId}")
 public DailyInventoryReport getSessionReport(@PathVariable Long sessionId) {
     return reportService.getSessionReport(sessionId);
 }

 /** Active session report — live view for bar owner */
 @GetMapping("/active")
 public DailyInventoryReport getActiveReport(@RequestParam Long barId) {
     return reportService.getActiveSessionReport(barId);
 }

 /** Summary list — filter by date range for bar owner history */
 @GetMapping("/bar/{barId}/summaries")
 public List<BarReportSummary> getSummaries(
     @PathVariable Long barId,
     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
 ) {
     return reportService.getBarReportSummaries(barId, from, to);
 }
}