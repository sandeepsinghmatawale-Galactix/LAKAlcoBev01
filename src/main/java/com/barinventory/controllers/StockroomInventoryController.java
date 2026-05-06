package com.barinventory.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.barinventory.config.SecurityUtils;
import com.barinventory.dtos.StockroomClosingRequest;
import com.barinventory.entities.StockroomInventory;
import com.barinventory.services.StockroomInventoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stockroom")
public class StockroomInventoryController {

    private final StockroomInventoryService stockroomService;

    // GET /stockroom/{sessionId}
    @GetMapping("/{sessionId}")
    public String stockroomPage(@PathVariable Long sessionId, Model model) {
        Long barId = SecurityUtils.getBarId();
        List<StockroomInventory> stocks =
                stockroomService.getStockroomByBarAndSession(barId, sessionId);
        model.addAttribute("stocks", stocks);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("barId", barId);
        return "stockroom/stockroom-inventory";
    }

    // POST /stockroom/closing/{sessionId}
    @PostMapping("/closing/{sessionId}")
    public String updateClosing(
            @PathVariable Long sessionId,
            @RequestParam List<Long> brandId,
            @RequestParam List<Integer> closingStock
    ) {
        Long barId = SecurityUtils.getBarId();

        List<StockroomClosingRequest> requests = new ArrayList<>();
        for (int i = 0; i < brandId.size(); i++) {
            StockroomClosingRequest req = new StockroomClosingRequest();
            req.setBrandId(brandId.get(i));
            req.setClosingStock(closingStock.get(i));
            requests.add(req);
        }

        // ✅ actually save — was missing before
        stockroomService.updateStockroomClosing(barId, sessionId, requests);

        return "redirect:/distribution/create-page/" + sessionId;
    }
}