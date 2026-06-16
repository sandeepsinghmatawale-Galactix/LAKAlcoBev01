// admin/services/DepotPackPriceHistoryService.java  (add publisher + publish call)
package com.barinventory.admin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.PriceUpdateRequest;
import com.barinventory.admin.entities.DepotBrandSizePack;
import com.barinventory.admin.entities.DepotPackPriceHistory;
import com.barinventory.admin.events.PackPriceChangedEvent;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotBrandSizePackRepository;
import com.barinventory.admin.repsitory.DepotPackPriceHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotPackPriceHistoryService {
	private final DepotPackPriceHistoryRepository priceHistoryRepository;
	private final DepotBrandSizePackRepository packRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public DepotPackPriceHistory updatePrice(Long packId, PriceUpdateRequest req) {
		DepotBrandSizePack pack = packRepository.findById(packId)
				.orElseThrow(() -> new ResourceNotFoundException("Pack not found: " + packId));

		LocalDateTime effectiveFrom = req.effectiveFrom() != null ? req.effectiveFrom() : LocalDateTime.now();

		priceHistoryRepository.findByPack_PackIdAndEffectiveToIsNull(packId).ifPresent(prev -> {
			if (!prev.getEffectiveFrom().isBefore(effectiveFrom)) {
				throw new IllegalArgumentException("New effectiveFrom must be after current price's effectiveFrom");
			}
			prev.setEffectiveTo(effectiveFrom);
			priceHistoryRepository.save(prev);
		});

		DepotPackPriceHistory newPrice = new DepotPackPriceHistory();
		newPrice.setPack(pack);
		newPrice.setMrp(req.mrp());
		newPrice.setExciseDuty(req.exciseDuty());
		newPrice.setBasePrice(req.basePrice());
		newPrice.setEffectiveFrom(effectiveFrom);
		newPrice.setEffectiveTo(null);
		newPrice.setRevisionReason(req.revisionReason());
		newPrice.setCreatedByAdminId(req.createdByAdminId());
		newPrice.setCreatedAt(LocalDateTime.now());
		DepotPackPriceHistory saved = priceHistoryRepository.save(newPrice);

		eventPublisher.publishEvent(new PackPriceChangedEvent(packId, req.mrp(), effectiveFrom));
		return saved;
	}

	public Optional<DepotPackPriceHistory> getCurrentPrice(Long packId) {
		return priceHistoryRepository.findByPack_PackIdAndEffectiveToIsNull(packId);
	}

	public Optional<DepotPackPriceHistory> getPriceAsOf(Long packId, LocalDateTime asOf) {
		List<DepotPackPriceHistory> rows = priceHistoryRepository.findActivePriceAsOf(packId, asOf);
		return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
	}

	public List<DepotPackPriceHistory> getHistory(Long packId) {
		return priceHistoryRepository.findByPack_PackIdOrderByEffectiveFromDesc(packId);
	}
}