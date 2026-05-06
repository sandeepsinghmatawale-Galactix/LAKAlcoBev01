package com.barinventory.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.barinventory.config.SecurityUtils;
import com.barinventory.dtos.DistributionRequest;
import com.barinventory.dtos.DistributionRequestWrapper;
import com.barinventory.entities.Brand;
import com.barinventory.entities.Distribution;
import com.barinventory.entities.Well;
import com.barinventory.services.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/distribution")
public class DistributionController {

    private final DistributionService distributionService;
    private final BrandService brandService;
    private final WellService wellService;
    private final StockroomInventoryService stockroomService;

    // GET /distribution/create-page/{sessionId}
    @GetMapping("/create-page/{sessionId}")
    public String createDistributionPage(@PathVariable Long sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "distribution/distribution-create";
    }

    // POST /distribution/create/{sessionId}
    @PostMapping("/create/{sessionId}")
    public String createDistribution(@PathVariable Long sessionId) {
        Long barId = SecurityUtils.getBarId();
        Distribution distribution =
                distributionService.createDistribution(barId, sessionId);
        return "redirect:/distribution/allocation/" + distribution.getDistributionId();
    }

    // GET /distribution/allocation/{distributionId}
    @GetMapping("/allocation/{distributionId}")
    public String allocationPage(@PathVariable Long distributionId, Model model) {
        Long barId = SecurityUtils.getBarId();

        List<Brand> brands = brandService.getBrandsByBar(barId);
        List<Well> wells = wellService.getWellsByBar(barId);
        Map<Long, Integer> stockMap =
                stockroomService.getSaleStockMap(distributionId);

        List<DistributionRequest> requests = new ArrayList<>();
        for (Brand brand : brands) {
            for (Well well : wells) {
                DistributionRequest req = new DistributionRequest();
                req.setBrandId(brand.getBrandId());
                req.setWellId(well.getWellId());
                req.setDistributedQty(0);
                requests.add(req);
            }
        }

        DistributionRequestWrapper wrapper = new DistributionRequestWrapper();
        wrapper.setRequests(requests);

        model.addAttribute("brands", brands);
        model.addAttribute("wells", wells);
        model.addAttribute("stockMap", stockMap);
        model.addAttribute("distributionId", distributionId);
        model.addAttribute("wrapper", wrapper);

        return "distribution/distribution-allocation";
    }

    // POST /distribution/allocate/{distributionId}
    @PostMapping("/allocate/{distributionId}")
    public String distribute(
            @PathVariable Long distributionId,
            @ModelAttribute DistributionRequestWrapper wrapper,
            Model model
    ) {
        Long barId = SecurityUtils.getBarId();

        try {
            distributionService.distributeStock(distributionId, wrapper.getRequests());

            Long sessionId =
                    distributionService.getSessionIdByDistribution(distributionId);

            return "redirect:/well/select/" + sessionId;

        } catch (RuntimeException ex) {
            ex.printStackTrace();

            List<Brand> brands = brandService.getBrandsByBar(barId);
            List<Well> wells = wellService.getWellsByBar(barId);
            Map<Long, Integer> stockMap =
                    stockroomService.getSaleStockMap(distributionId);

            model.addAttribute("brands", brands);
            model.addAttribute("wells", wells);
            model.addAttribute("stockMap", stockMap);
            model.addAttribute("distributionId", distributionId);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("wrapper", wrapper);

            return "distribution/distribution-allocation";
        }
    }
}