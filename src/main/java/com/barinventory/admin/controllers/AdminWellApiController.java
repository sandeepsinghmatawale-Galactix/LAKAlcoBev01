package com.barinventory.admin.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barinventory.admin.dtos.AdminWellRequest;
import com.barinventory.inventory.entities.Well;
import com.barinventory.inventory.repos.WellRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/api/wells")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminWellApiController {

	private final WellRepository wellRepository;

	@GetMapping("/{barId}")
	public List<Well> getWells(@PathVariable Long barId) {
		return wellRepository.findByBarId(barId);
	}

	@PostMapping
	@Transactional
	public Well addWell(@RequestBody AdminWellRequest request) {

		if (request.barId() == null) {
			throw new IllegalArgumentException("barId is required");
		}

		if (request.wellName() == null || request.wellName().isBlank()) {
			throw new IllegalArgumentException("wellName is required");
		}

		String cleanedName = request.wellName().trim();

		return wellRepository.findByBarIdAndWellNameIgnoreCase(request.barId(), cleanedName).map(existing -> {
			existing.setActive(true);
			return wellRepository.save(existing);
		}).orElseGet(() -> {
			Well well = new Well();
			well.setBarId(request.barId());
			well.setWellName(cleanedName);
			well.setActive(true);
			return wellRepository.save(well);
		});
	}

	@PatchMapping("/{wellId}/activate")
	@Transactional
	public Well activateWell(@PathVariable Long wellId) {
		Well well = wellRepository.findById(wellId)
				.orElseThrow(() -> new IllegalArgumentException("Well not found: " + wellId));

		well.setActive(true);
		return wellRepository.save(well);
	}

	@PatchMapping("/{wellId}/deactivate")
	@Transactional
	public Well deactivateWell(@PathVariable Long wellId) {
		Well well = wellRepository.findById(wellId)
				.orElseThrow(() -> new IllegalArgumentException("Well not found: " + wellId));

		well.setActive(false);
		return wellRepository.save(well);
	}
}