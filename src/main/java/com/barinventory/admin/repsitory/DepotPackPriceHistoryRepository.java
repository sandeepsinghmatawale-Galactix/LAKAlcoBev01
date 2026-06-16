package com.barinventory.admin.repsitory;

//admin/repositories/DepotPackPriceHistoryRepository.java

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barinventory.admin.entities.DepotPackPriceHistory;

public interface DepotPackPriceHistoryRepository extends JpaRepository<DepotPackPriceHistory, Long> {

	Optional<DepotPackPriceHistory> findByPack_PackIdAndEffectiveToIsNull(Long packId);

	List<DepotPackPriceHistory> findByPack_PackIdOrderByEffectiveFromDesc(Long packId);

	@Query("SELECT p FROM DepotPackPriceHistory p WHERE p.pack.packId = :packId "
			+ "AND p.effectiveFrom <= :asOf AND (p.effectiveTo IS NULL OR p.effectiveTo > :asOf) "
			+ "ORDER BY p.effectiveFrom DESC")
	List<DepotPackPriceHistory> findActivePriceAsOf(@Param("packId") Long packId, @Param("asOf") LocalDateTime asOf);
}