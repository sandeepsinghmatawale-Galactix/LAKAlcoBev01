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
@Table(name = "depot_manufacturer")
@Getter
@Setter
public class DepotManufacturer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long manufacturerId;
	@Column(nullable = false)
	private String manufacturerName; // United Spirits, Pernod Ricard
	private String licenseNo;
	private String contactInfo;
}