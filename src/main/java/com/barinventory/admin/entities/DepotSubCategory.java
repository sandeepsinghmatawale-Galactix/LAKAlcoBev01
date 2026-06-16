package com.barinventory.admin.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depot_subcategory")
@Getter
@Setter
public class DepotSubCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subCategoryId;
	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private DepotCategory category;
	@Column(nullable = false)
	private String subCategoryName; // Whisky, Rum, Vodka, Gin, Brandy
}