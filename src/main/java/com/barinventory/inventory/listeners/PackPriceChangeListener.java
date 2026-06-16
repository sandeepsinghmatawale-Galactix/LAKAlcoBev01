package com.barinventory.inventory.listeners;

//inventory/listeners/PackPriceChangeListener.java

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.barinventory.admin.events.PackPriceChangedEvent;
import com.barinventory.inventory.services.BarProductPricingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PackPriceChangeListener {
	private final BarProductPricingService pricingService;

	@EventListener
	public void onPriceChanged(PackPriceChangedEvent event) {
		pricingService.syncMrpForPack(event.getPackId(), event.getNewMrp());
	}
}