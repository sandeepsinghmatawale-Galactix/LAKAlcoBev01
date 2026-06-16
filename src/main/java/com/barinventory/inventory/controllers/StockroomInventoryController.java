package com.barinventory.inventory.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.barinventory.config.SecurityUtils;
import com.barinventory.inventory.dtos.StockroomClosingRequest;
import com.barinventory.inventory.dtos.StockroomInventoryView;
import com.barinventory.inventory.entities.BarProductPricing;
import com.barinventory.inventory.entities.StockroomInventory;
import com.barinventory.inventory.repos.BarProductPricingRepository;
import com.barinventory.inventory.services.StockroomInventoryService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stockroom")
public class StockroomInventoryController {

    private final StockroomInventoryService stockroomService;
    private final BarProductPricingRepository productPricingRepo;

    /*
     -----------------------------------------
     STOCKROOM PAGE
     -----------------------------------------
    */
    @GetMapping("/{sessionId}")
    public String stockroomPage(
            @PathVariable Long sessionId,
            Model model
    ) {
        Long barId = SecurityUtils.getBarId();

        List<StockroomInventory> stocks =
                stockroomService.getStockroomByBarAndSession(barId, sessionId);

        // Build a lookup map: depotBrandSizeId → BarProductPricing
        Map<Long, BarProductPricing> pricingMap = productPricingRepo.findByBarId(barId)
                .stream()
                .collect(Collectors.toMap(
                        BarProductPricing::getDepotBrandSizeId,
                        p -> p
                ));

        // Map to DTO using cached fields from BarProductPricing
        List<StockroomInventoryView> stockViews = stocks.stream().map(stock -> {
            BarProductPricing pricing = pricingMap.get(stock.getDepotBrandSizeId());

            StockroomInventoryView view = new StockroomInventoryView();
            view.setStockroomId(stock.getStockroomId());
            view.setDepotBrandSizeId(stock.getDepotBrandSizeId());
            view.setCachedBrandName(pricing != null ? pricing.getCachedBrandName() : "Unknown");
            view.setCachedSizeMl(pricing != null ? pricing.getCachedSizeMl() : 0);
            view.setOpeningStock(stock.getOpeningStock());
            view.setReceivedStock(stock.getReceivedStock());
            view.setClosingStock(stock.getClosingStock());
            view.setSaleStock(stock.getSaleStock());
            return view;
        }).toList();

        model.addAttribute("stocks", stockViews);  // ✅ now passes DTOs, not raw entities
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("barId", barId);
        return "stockroom/stockroom-inventory";
    }

    /*
     -----------------------------------------
     UPDATE CLOSING
     -----------------------------------------
    */
    @PostMapping("/closing/{sessionId}")
    public String updateClosing(
            @PathVariable Long sessionId,
            @RequestParam List<Long> brandSizeId,
            @RequestParam List<Integer> closingStock
    ) {
        Long barId = SecurityUtils.getBarId();

        List<StockroomClosingRequest> requests = new ArrayList<>();
        for (int i = 0; i < brandSizeId.size(); i++) {
            StockroomClosingRequest req = new StockroomClosingRequest();
            req.setBrandSizeId(brandSizeId.get(i));
            req.setClosingStock(closingStock.get(i));
            requests.add(req);
        }

        stockroomService.updateStockroomClosing(barId, sessionId, requests);
        return "redirect:/distribution/create-page/" + sessionId;
    }
}