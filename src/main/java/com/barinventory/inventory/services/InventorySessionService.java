package com.barinventory.inventory.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barinventory.auth.entities.Bar;
import com.barinventory.auth.repos.BarRepository;
import com.barinventory.inventory.entities.BarProductPricing;
import com.barinventory.inventory.entities.InventorySession;
import com.barinventory.inventory.entities.SessionStatus;
import com.barinventory.inventory.entities.StockroomInventory;
import com.barinventory.inventory.repos.BarProductPricingRepository;
import com.barinventory.inventory.repos.InventorySessionRepository;
import com.barinventory.inventory.repos.StockroomInventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventorySessionService {

    private final InventorySessionRepository sessionRepo;
    private final StockroomInventoryRepository stockroomRepo;
    private final BarProductPricingRepository productPricingRepo;
    private final BarRepository barRepository;

    public InventorySession createSession(Long barId, String sessionName) {
        if (sessionRepo.existsByBarBarIdAndStatus(barId, SessionStatus.OPEN)) {
            throw new RuntimeException("An OPEN session already exists for this establishment.");
        }

        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new RuntimeException("Bar not found with id: " + barId));

        InventorySession session = new InventorySession();
        session.setSessionName(sessionName);
        session.setSessionDate(LocalDateTime.now());
        session.setStatus(SessionStatus.OPEN);
        session.setBar(bar);

        InventorySession savedSession = sessionRepo.save(session);

        // ✅ Check for previous CLOSED session to carry forward closing stock
        Optional<InventorySession> previousSession = sessionRepo
                .findTopByBarBarIdAndStatusOrderBySessionIdDesc(barId, SessionStatus.CLOSED);

        if (previousSession.isPresent()) {
            // ✅ Carry forward: previous closing stock → new opening stock
            List<StockroomInventory> previousStocks = stockroomRepo
                    .findBySessionSessionId(previousSession.get().getSessionId());

            // Build map of depotBrandSizeId → closingStock from previous session
            Map<Long, Integer> closingMap = previousStocks.stream()
                    .collect(Collectors.toMap(
                            StockroomInventory::getDepotBrandSizeId,
                            s -> s.getClosingStock() != null ? s.getClosingStock() : 0
                    ));

            List<BarProductPricing> assignedProducts = productPricingRepo.findByBarId(barId);

            List<StockroomInventory> stocks = assignedProducts.stream().map(product -> {
                StockroomInventory s = new StockroomInventory();
                s.setSession(savedSession);
                s.setBarId(barId);
                s.setDepotBrandSizeId(product.getDepotBrandSizeId());
                // ✅ Use previous closing stock as opening stock, default 0 if first time
                s.setOpeningStock(closingMap.getOrDefault(product.getDepotBrandSizeId(), 0));
                s.setReceivedStock(0);
                s.setClosingStock(0);
                s.setSaleStock(0);
                return s;
            }).toList();

            stockroomRepo.saveAll(stocks);

        } else {
        	// First session — check for admin-seeded opening stock (session=null rows)
            List<BarProductPricing> assignedProducts = productPricingRepo.findByBarId(barId);

            List<StockroomInventory> stocks = assignedProducts.stream().map(product -> {
                // Look for admin-seeded opening stock row
                Integer seedQty = stockroomRepo
                    .findByBarIdAndDepotBrandSizeIdAndSessionIsNull(barId, product.getDepotBrandSizeId())
                    .map(StockroomInventory::getClosingStock)
                    .orElse(0);

                StockroomInventory s = new StockroomInventory();
                s.setSession(savedSession);
                s.setBarId(barId);
                s.setDepotBrandSizeId(product.getDepotBrandSizeId());
                s.setOpeningStock(seedQty); // carry admin-seeded qty
                s.setReceivedStock(0);
                s.setClosingStock(0);
                s.setSaleStock(0);
                return s;
            }).toList();

            stockroomRepo.saveAll(stocks);
    }
        return savedSession;
    }

    public void closeSession(Long sessionId) {
        InventorySession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.getStatus() == SessionStatus.CLOSED) {
            throw new RuntimeException("Session already closed");
        }
        session.setStatus(SessionStatus.CLOSED);
    }

    public List<InventorySession> getAllSessionsForBar(Long barId) {
        return sessionRepo.findByBarBarId(barId);
    }

    public Optional<InventorySession> getActiveSession(Long barId) {
        return sessionRepo.findByBarBarIdAndStatus(barId, SessionStatus.OPEN);
    }

    public Optional<InventorySession> getLatestSessionByBar(Long barId, SessionStatus status) {
        return sessionRepo.findTopByBarBarIdAndStatusOrderBySessionIdDesc(barId, status);
    }

    public boolean hasActiveSession(Long barId) {
        return sessionRepo.existsByBarBarIdAndStatus(barId, SessionStatus.OPEN);
    }
}