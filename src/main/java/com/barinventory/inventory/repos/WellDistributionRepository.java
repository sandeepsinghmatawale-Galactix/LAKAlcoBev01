package com.barinventory.inventory.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.barinventory.inventory.entities.WellDistribution;

@Repository
public interface WellDistributionRepository extends JpaRepository<WellDistribution, Long> {

	List<WellDistribution> findByDistributionDistributionId(Long distributionId);

	List<WellDistribution> findByWellWellId(Long wellId);

	List<WellDistribution> findByWellWellIdAndDepotBrandSizeId(Long wellId, Long depotBrandSizeId);

	List<WellDistribution> findByDepotBrandSizeId(Long depotBrandSizeId);

	Optional<WellDistribution> findByDistributionDistributionIdAndWellWellIdAndDepotBrandSizeId(Long distributionId,
			Long wellId, Long depotBrandSizeId);

	@Query("""
			SELECT COALESCE(SUM(w.distributedQty), 0)
			FROM WellDistribution w
			WHERE w.distribution.distributionId = :distributionId
			AND w.depotBrandSizeId = :depotBrandSizeId
			""")
	Integer getTotalDistributedQty(@Param("distributionId") Long distributionId,
			@Param("depotBrandSizeId") Long depotBrandSizeId);

	@Modifying
	@Query("DELETE FROM WellDistribution wd WHERE wd.distribution.distributionId = :distributionId")
	void deleteByDistributionId(@Param("distributionId") Long distributionId);

	@Query("""
			SELECT wd FROM WellDistribution wd
			JOIN wd.distribution d
			WHERE wd.well.wellId = :wellId
			AND d.session.sessionId = :sessionId
			""")
	List<WellDistribution> findByWellIdAndSessionId(@Param("wellId") Long wellId, @Param("sessionId") Long sessionId);

	@Query("""
			SELECT wd FROM WellDistribution wd
			JOIN wd.distribution d
			JOIN d.session s
			WHERE wd.well.wellId = :wellId
			AND s.sessionId = :sessionId
			AND s.bar.barId = :barId
			""")
	List<WellDistribution> findByWellSessionAndBar(@Param("wellId") Long wellId, @Param("sessionId") Long sessionId,
			@Param("barId") Long barId);
}