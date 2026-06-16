package com.barinventory.inventory.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.barinventory.config.SecurityUtils;
import com.barinventory.inventory.dtos.DistributionRequest;
import com.barinventory.inventory.dtos.DistributionRequestWrapper;
import com.barinventory.inventory.entities.BarProductPricing;
import com.barinventory.inventory.entities.Distribution;
import com.barinventory.inventory.entities.Well;
import com.barinventory.inventory.repos.BarProductPricingRepository;
import com.barinventory.inventory.services.DistributionService;
import com.barinventory.inventory.services.StockroomInventoryService;
import com.barinventory.inventory.services.WellService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/distribution")
public class DistributionController {

    private final DistributionService distributionService;
    private final BarProductPricingRepository productPricingRepo;  // ✅ replaced BrandService
    private final WellService wellService;
    private final StockroomInventoryService stockroomService;

    @GetMapping("/create-page/{sessionId}")
    public String createDistributionPage(@PathVariable Long sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "distribution/distribution-create";
    }

    @PostMapping("/create/{sessionId}")
    public String createDistribution(@PathVariable Long sessionId) {
        Long barId = SecurityUtils.getBarId();
        Distribution distribution = distributionService.createDistribution(barId, sessionId);
        return "redirect:/distribution/allocation/" + distribution.getDistributionId();
    }

    @GetMapping("/allocation/{distributionId}")
    public String allocationPage(@PathVariable Long distributionId, Model model) {
        Long barId = SecurityUtils.getBarId();

        // ✅ Use BarProductPricing instead of old Brand/BrandSize
        List<BarProductPricing> pricings = productPricingRepo.findByBarId(barId);
        List<Well> wells = wellService.getWellsByBar(barId);
        if (wells.isEmpty()) wells = wellService.getAllWells();

        Map<Long, Integer> stockMap = stockroomService.getSaleStockMap(distributionId);

        List<DistributionRequest> requests = new ArrayList<>();
        Map<String, Integer> indexMap = new HashMap<>();
        int idx = 0;

        for (BarProductPricing pricing : pricings) {
            // Only show products that have sale stock
            if (!stockMap.containsKey(pricing.getDepotBrandSizeId())) continue;

            for (Well well : wells) {
                DistributionRequest req = new DistributionRequest();
                req.setDepotBrandId(pricing.getDepotBrandId());
                req.setDepotBrandSizeId(pricing.getDepotBrandSizeId());
                req.setWellId(well.getWellId());
                req.setDistributedQty(0);
                requests.add(req);
                indexMap.put(pricing.getDepotBrandSizeId() + "_" + well.getWellId(), idx++);
            }
        }

        DistributionRequestWrapper wrapper = new DistributionRequestWrapper();
        wrapper.setRequests(requests);

        Long sessionId = distributionService.getSessionIdByDistribution(distributionId);

        model.addAttribute("pricings", pricings);       // ✅ replaces brands
        model.addAttribute("wells", wells);
        model.addAttribute("stockMap", stockMap);
        model.addAttribute("indexMap", indexMap);
        model.addAttribute("distributionId", distributionId);
        model.addAttribute("wrapper", wrapper);
        model.addAttribute("sessionId", sessionId);

        return "distribution/distribution-allocation";
    }

    @PostMapping("/allocate/{distributionId}")
    public String distribute(
            @PathVariable Long distributionId,
            @ModelAttribute DistributionRequestWrapper wrapper,
            Model model
    ) {
        Long barId = SecurityUtils.getBarId();

        try {
            distributionService.distributeStock(distributionId, wrapper.getRequests());
            Long sessionId = distributionService.getSessionIdByDistribution(distributionId);
            return "redirect:/well/select/" + sessionId;

        } catch (RuntimeException ex) {
            ex.printStackTrace();

            List<BarProductPricing> pricings = productPricingRepo.findByBarId(barId);
            List<Well> wells = wellService.getWellsByBar(barId);
            Map<Long, Integer> stockMap = stockroomService.getSaleStockMap(distributionId);

            model.addAttribute("pricings", pricings);
            model.addAttribute("wells", wells);
            model.addAttribute("stockMap", stockMap);
            model.addAttribute("distributionId", distributionId);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("wrapper", wrapper);

            return "distribution/distribution-allocation";
        }
    }
}