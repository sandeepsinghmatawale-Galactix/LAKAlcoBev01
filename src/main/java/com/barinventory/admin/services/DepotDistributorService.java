package com.barinventory.admin.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.barinventory.admin.dtos.DistributorDto;
import com.barinventory.admin.entities.DepotDistributor;
import com.barinventory.admin.exceptions.ResourceNotFoundException;
import com.barinventory.admin.repsitory.DepotDistributorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepotDistributorService {
	private final DepotDistributorRepository repository;

	@Transactional
	public DistributorDto create(DistributorDto dto) {
		DepotDistributor entity = new DepotDistributor();
		apply(entity, dto);
		return toDto(repository.save(entity));
	}

	@Transactional
	public DistributorDto update(Long id, DistributorDto dto) {
		DepotDistributor entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Distributor not found: " + id));
		apply(entity, dto);
		return toDto(repository.save(entity));
	}

	public List<DistributorDto> getAll() {
		return repository.findAll().stream().map(this::toDto).toList();
	}

	private void apply(DepotDistributor entity, DistributorDto dto) {
		entity.setDistributorName(dto.distributorName());
		entity.setLicenseNo(dto.licenseNo());
		entity.setContactInfo(dto.contactInfo());
		entity.setRegion(dto.region());
	}

	private DistributorDto toDto(DepotDistributor d) {
		return new DistributorDto(d.getDistributorId(), d.getDistributorName(), d.getLicenseNo(), d.getContactInfo(),
				d.getRegion());
	}
}