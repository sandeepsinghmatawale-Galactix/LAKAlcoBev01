package com.barinventory.inventory.repos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.barinventory.inventory.entities.StockroomInventory;

@Repository
public interface StockroomInventoryRepository extends JpaRepository<StockroomInventory, Long> {

    List<StockroomInventory> findBySessionSessionId(Long sessionId);

    Optional<StockroomInventory> findBySessionSessionIdAndDepotBrandSizeId(Long sessionId, Long depotBrandSizeId);

    @Query("SELECT s FROM StockroomInventory s WHERE s.session.sessionId = :sessionId")
    List<StockroomInventory> getPreviousSessionStocks(@Param("sessionId") Long sessionId);

    @Query("SELECT s FROM StockroomInventory s WHERE s.session.sessionId = :sessionId AND s.saleStock > 0")
    List<StockroomInventory> findDistributableStocks(@Param("sessionId") Long sessionId);

    @Query("""
        SELECT s FROM StockroomInventory s
        JOIN BarProductPricing bpp ON s.depotBrandSizeId = bpp.depotBrandSizeId AND s.barId = bpp.barId
        WHERE s.session.sessionId = :sessionId
        """)
    List<StockroomInventory> findBySessionWithBrandSize(@Param("sessionId") Long sessionId);

    List<StockroomInventory> findByBarIdAndSessionSessionId(Long barId, Long sessionId);

    boolean existsByBarIdAndSessionSessionIdAndDepotBrandSizeId(Long barId, Long sessionId, Long depotBrandSizeId);

    Optional<StockroomInventory> findByBarIdAndSessionSessionIdAndDepotBrandSizeId(Long barId, Long sessionId, Long depotBrandSizeId);
    
    List<StockroomInventory> findByBarId(Long barId);
    Optional<StockroomInventory> findByBarIdAndDepotBrandSizeId(Long barId, Long depotBrandSizeId);
    
    Optional<StockroomInventory> findByBarIdAndDepotBrandSizeIdAndSessionIsNull(
    	    Long barId, Long depotBrandSizeId);
 
 
    
}
