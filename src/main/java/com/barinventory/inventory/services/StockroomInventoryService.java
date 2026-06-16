package com.barinventory.inventory.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.barinventory.inventory.dtos.StockroomClosingRequest;
import com.barinventory.inventory.entities.InventorySession;
import com.barinventory.inventory.entities.StockroomInventory;
import com.barinventory.inventory.repos.DistributionRepository;
import com.barinventory.inventory.repos.InventorySessionRepository;
import com.barinventory.inventory.repos.StockroomInventoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StockroomInventoryService {

    private final StockroomInventoryRepository stockroomRepo;
    private final InventorySessionRepository sessionRepo;
    private final DistributionRepository distributionRepo;

    
    public void initializeStockroom(Long currentSessionId, Long previousSessionId) {
        List<StockroomInventory> previousStocks = stockroomRepo.findBySessionSessionId(previousSessionId);
        InventorySession currentSession = sessionRepo.findById(currentSessionId)
                .orElseThrow(() -> new RuntimeException("Current operational session context missing."));

        Long barId = currentSession.getBar().getBarId();

        for (StockroomInventory previous : previousStocks) {
            boolean exists = stockroomRepo.existsByBarIdAndSessionSessionIdAndDepotBrandSizeId(
                    barId, currentSessionId, previous.getDepotBrandSizeId()); // ✅ fixed

            if (exists) continue;

            StockroomInventory current = new StockroomInventory();
            current.setSession(currentSession);
            current.setBarId(barId);
            current.setDepotBrandSizeId(previous.getDepotBrandSizeId()); // ✅ fixed
            current.setOpeningStock(previous.getClosingStock());
            current.setReceivedStock(0);
            current.setClosingStock(0);
            current.setSaleStock(0);

            stockroomRepo.save(current);
        }
    }

    public void save(StockroomInventory stock) {
        stockroomRepo.save(stock);
    }

    public List<StockroomInventory> getStockroomByBarAndSession(Long barId, Long sessionId) {
        return stockroomRepo.findByBarIdAndSessionSessionId(barId, sessionId);
    }

    public List<StockroomInventory> getStockroomBySession(Long sessionId) {
        return stockroomRepo.findBySessionSessionId(sessionId);
    }

    public void updateStockroomClosing(Long barId, Long sessionId, List<StockroomClosingRequest> requests) {
        InventorySession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getBar().getBarId().equals(barId)) {
            throw new RuntimeException("Session execution context mismatch: profile does not match target corporate location.");
        }

        for (StockroomClosingRequest request : requests) {
            StockroomInventory stock = stockroomRepo
                    .findBySessionSessionIdAndDepotBrandSizeId(sessionId, request.getBrandSizeId()) // ✅ fixed
                    .orElseThrow(() -> new RuntimeException("Stock target profile item not located in backroom ledger."));

            int totalAvailable = stock.getOpeningStock() + stock.getReceivedStock();

            if (request.getClosingStock() > totalAvailable) {
                throw new RuntimeException("Invalid closing stock entry for Brand Size ID [ " + stock.getDepotBrandSizeId() + " ]. " // ✅ fixed
                        + "Counted quantity exceeds calculated maximum total available unit volume.");
            }

            stock.setClosingStock(request.getClosingStock());
            stock.setSaleStock(totalAvailable - request.getClosingStock());
        }
    }

    public Map<Long, Integer> getSaleStockMap(Long distributionId) {
        Long sessionId = distributionRepo.findById(distributionId)
                .orElseThrow(() -> new RuntimeException("Target distribution transaction window not found."))
                .getSession().getSessionId();

        List<StockroomInventory> stocks = stockroomRepo.findBySessionSessionId(sessionId);
        Map<Long, Integer> stockMap = new HashMap<>();

        for (StockroomInventory stock : stocks) {
            int available = stock.getSaleStock() > 0
                ? stock.getSaleStock()
                : (stock.getOpeningStock() + stock.getReceivedStock() - stock.getClosingStock());

            if (available > 0) {
                stockMap.put(stock.getDepotBrandSizeId(), available); // ✅ fixed
            }
        }
        return stockMap;
    }
}