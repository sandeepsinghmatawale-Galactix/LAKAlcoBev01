package com.barinventory.inventory.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.barinventory.inventory.dtos.DistributionRequest;
import com.barinventory.inventory.entities.Distribution;
import com.barinventory.inventory.entities.InventorySession;
import com.barinventory.inventory.entities.StockroomInventory;
import com.barinventory.inventory.entities.WellDistribution;
import com.barinventory.inventory.repos.DistributionRepository;
import com.barinventory.inventory.repos.InventorySessionRepository;
import com.barinventory.inventory.repos.StockroomInventoryRepository;
import com.barinventory.inventory.repos.WellDistributionRepository;
import com.barinventory.inventory.repos.WellRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DistributionService {

    private final DistributionRepository distributionRepo;
    private final WellDistributionRepository wellDistributionRepo;
    private final StockroomInventoryRepository stockroomRepo;
    private final WellRepository wellRepo;
    private final InventorySessionRepository sessionRepo;

    public Distribution createDistribution(Long barId, Long sessionId) {
        InventorySession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getBar().getBarId().equals(barId)) {
            throw new RuntimeException("Session does not belong to this bar");
        }

        Distribution distribution = new Distribution();
        distribution.setSession(session);
        distribution.setDistributedAt(LocalDateTime.now());

        return distributionRepo.save(distribution);
    }

    public void distributeStock(Long distributionId, List<DistributionRequest> requests) {
        validateInput(requests);

        Distribution distribution = distributionRepo.findById(distributionId)
                .orElseThrow(() -> new RuntimeException("Distribution not found"));

        Long sessionId = distribution.getSession().getSessionId();
        List<StockroomInventory> stocks = stockroomRepo.findDistributableStocks(sessionId);

        validateAgainstStock(requests, stocks);

        List<WellDistribution> batchList = prepareBatch(requests, distribution);

        wellDistributionRepo.deleteByDistributionId(distributionId);
        wellDistributionRepo.saveAll(batchList);
        wellDistributionRepo.flush();

        validateDistribution(sessionId, distributionId);
    }

    private void validateInput(List<DistributionRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new RuntimeException("No distribution data submitted");
        }
        for (DistributionRequest r : requests) {
            if (r.getDistributedQty() == null || r.getDistributedQty() <= 0) continue;
            if (r.getDepotBrandSizeId() == null || r.getDepotBrandSizeId() == 0) continue;
            if (r.getWellId() == null || r.getWellId() == 0) continue;
            if (r.getDistributedQty() < 0) {
                throw new RuntimeException("Negative quantity not allowed");
            }
        }
    }

    private void validateAgainstStock(List<DistributionRequest> requests, List<StockroomInventory> stocks) {
        Map<Long, Integer> totalMap = new HashMap<>();

        for (DistributionRequest r : requests) {
            if (r.getDistributedQty() == null || r.getDistributedQty() <= 0) continue;
            if (r.getDepotBrandSizeId() == null || r.getDepotBrandSizeId() == 0) continue;
            totalMap.merge(r.getDepotBrandSizeId(), r.getDistributedQty(), Integer::sum);
        }

        for (StockroomInventory stock : stocks) {
            if (stock.getSaleStock() == null || stock.getSaleStock() == 0) continue;
            Long depotBrandSizeId = stock.getDepotBrandSizeId();
            int actual = totalMap.getOrDefault(depotBrandSizeId, 0);
            if (actual != stock.getSaleStock()) {
                throw new RuntimeException("Distribution mismatch for Brand Size ID [ "
                        + depotBrandSizeId + " ] | Expected=" + stock.getSaleStock()
                        + " | Distributed=" + actual);
            }
        }
    }

    private List<WellDistribution> prepareBatch(List<DistributionRequest> requests, Distribution distribution) {
        List<WellDistribution> list = new ArrayList<>();

        for (DistributionRequest r : requests) {
            if (r.getDistributedQty() == null || r.getDistributedQty() <= 0) continue;
            if (r.getDepotBrandSizeId() == null || r.getWellId() == null) continue;

            WellDistribution wd = new WellDistribution();
            wd.setDistribution(distribution);
            wd.setDepotBrandSizeId(r.getDepotBrandSizeId());   // ✅ fixed
            wd.setDepotBrandId(r.getDepotBrandSizeId());            // ✅ fixed
            wd.setWell(wellRepo.getReferenceById(r.getWellId()));
            wd.setDistributedQty(r.getDistributedQty());
            wd.setDistributedAt(LocalDateTime.now());

            list.add(wd);
        }
        return list;
    }

    private void validateDistribution(Long sessionId, Long distributionId) {
        List<StockroomInventory> stocks = stockroomRepo.findDistributableStocks(sessionId);

        for (StockroomInventory stock : stocks) {
            if (stock.getSaleStock() == null || stock.getSaleStock() == 0) continue;
            Integer distributedQty = wellDistributionRepo.getTotalDistributedQty(
                    distributionId, stock.getDepotBrandSizeId());  // ✅ fixed
            if (distributedQty == null) distributedQty = 0;
            if (!distributedQty.equals(stock.getSaleStock())) {
                throw new RuntimeException("Final validation failed for Brand Size ID [ "
                        + stock.getDepotBrandSizeId() + " ]");
            }
        }
    }

    public Long getSessionIdByDistribution(Long distributionId) {
        return distributionRepo.findById(distributionId)
                .map(d -> d.getSession().getSessionId())
                .orElseThrow(() -> new RuntimeException("Distribution not found"));
    }
}