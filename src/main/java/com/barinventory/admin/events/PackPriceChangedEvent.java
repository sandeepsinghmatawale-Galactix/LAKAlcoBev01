package com.barinventory.admin.events;

//admin/events/PackPriceChangedEvent.java
 

import java.time.LocalDateTime;

public class PackPriceChangedEvent {
 private final Long packId;
 private final Double newMrp;
 private final LocalDateTime effectiveFrom;

 public PackPriceChangedEvent(Long packId, Double newMrp, LocalDateTime effectiveFrom) {
     this.packId = packId;
     this.newMrp = newMrp;
     this.effectiveFrom = effectiveFrom;
 }

 public Long getPackId() { return packId; }
 public Double getNewMrp() { return newMrp; }
 public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
}