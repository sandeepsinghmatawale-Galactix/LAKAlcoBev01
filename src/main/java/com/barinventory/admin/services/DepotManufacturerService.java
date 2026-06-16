package com.barinventory.admin.services;

//admin/services/DepotManufacturerService.java

import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.ManufacturerDto;
import com.barinventory.admin.entities.DepotManufacturer;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotManufacturerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotManufacturerService {
	private final DepotManufacturerRepository repository;

	@Transactional
	public ManufacturerDto create(ManufacturerDto dto) {
		DepotManufacturer entity = new DepotManufacturer();
		apply(entity, dto);
		return toDto(repository.save(entity));
	}

	@Transactional
	public ManufacturerDto update(Long id, ManufacturerDto dto) {
		DepotManufacturer entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Manufacturer not found: " + id));
		apply(entity, dto);
		return toDto(repository.save(entity));
	}

	public List<ManufacturerDto> getAll() {
		return repository.findAll().stream().map(this::toDto).toList();
	}

	private void apply(DepotManufacturer entity, ManufacturerDto dto) {
		entity.setManufacturerName(dto.manufacturerName());
		entity.setLicenseNo(dto.licenseNo());
		entity.setContactInfo(dto.contactInfo());
	}

	private ManufacturerDto toDto(DepotManufacturer m) {
		return new ManufacturerDto(m.getManufacturerId(), m.getManufacturerName(), m.getLicenseNo(),
				m.getContactInfo());
	}
}