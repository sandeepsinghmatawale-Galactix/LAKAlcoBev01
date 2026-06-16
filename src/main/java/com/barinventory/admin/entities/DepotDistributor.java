package com.barinventory.admin.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depot_distributor")
@Setter
@Getter
public class DepotDistributor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long distributorId;
	@Column(nullable = false)
	private String distributorName;
	private String licenseNo;
	private String contactInfo;
	private String region; // depot/zone they supply
}
