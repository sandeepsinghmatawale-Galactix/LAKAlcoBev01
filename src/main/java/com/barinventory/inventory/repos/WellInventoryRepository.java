package com.barinventory.inventory.repos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.barinventory.inventory.entities.WellInventory;
import jakarta.persistence.LockModeType;

@Repository
public interface WellInventoryRepository extends JpaRepository<WellInventory, Long> {

    boolean existsByWellWellIdAndProductPricingDepotBrandSizeId(
            Long wellId,
            Long depotBrandSizeId
    );

    Optional<WellInventory> findByBarIdAndSessionSessionIdAndWellWellIdAndProductPricingDepotBrandSizeId(
            Long barId,
            Long sessionId,
            Long wellId,
            Long depotBrandSizeId
    );

    List<WellInventory> findByWellWellIdAndSessionSessionId(Long wellId, Long sessionId);

    List<WellInventory> findBySessionSessionId(Long sessionId);

    @Query("SELECT wi FROM WellInventory wi JOIN FETCH wi.well WHERE wi.session.sessionId = :sessionId")
    List<WellInventory> findBySessionSessionIdWithWell(@Param("sessionId") Long sessionId);

    List<WellInventory> findByBarIdAndSessionSessionId(Long barId, Long sessionId);

    List<WellInventory> findByBarIdAndSessionSessionIdAndWellWellId(Long barId, Long sessionId, Long wellId);

    @Query("""
            SELECT wi FROM WellInventory wi
            WHERE wi.barId = :barId
              AND wi.well.wellId = :wellId
              AND wi.session.sessionId = (
                  SELECT MAX(wi2.session.sessionId)
                  FROM WellInventory wi2
                  WHERE wi2.barId = :barId
                    AND wi2.well.wellId = :wellId
                    AND wi2.session.sessionId < :currentSessionId
              )
            """)
    List<WellInventory> getPreviousWellInventory(
            @Param("barId") Long barId,
            @Param("wellId") Long wellId,
            @Param("currentSessionId") Long currentSessionId
    );

    @Query("""
            SELECT wi FROM WellInventory wi
            WHERE wi.barId = :barId
              AND wi.session.sessionId = :sessionId
              AND wi.well.wellId = :wellId
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<WellInventory> lockAndFindByBarSessionWell(
            @Param("barId") Long barId,
            @Param("sessionId") Long sessionId,
            @Param("wellId") Long wellId
    );
    
    long countByBarIdAndSessionSessionIdAndWellWellId(Long barId, Long sessionId, Long wellId);
    
    List<WellInventory> findByBarId(Long barId);
    List<WellInventory> findByWell_WellId(Long wellId);
    Optional<WellInventory> findByBarIdAndWellWellIdAndProductPricingIdAndSessionIsNull(
    	    Long barId, Long wellId, Long productPricingId);
    
    List<WellInventory> findByBarIdAndWellWellIdAndSessionIsNull(Long barId, Long wellId);
}