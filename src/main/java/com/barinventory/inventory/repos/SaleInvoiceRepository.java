package com.barinventory.inventory.repos;

//inventory/repositories/SaleInvoiceRepository.java
 

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.inventory.entities.SaleInvoice;

public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Long> {
 Optional<SaleInvoice> findByInvoiceNumber(String invoiceNumber);
 List<SaleInvoice> findByBarIdAndSaleTimestampBetween(Long barId, LocalDateTime from, LocalDateTime to);
 List<SaleInvoice> findByBarId(Long barId);
}