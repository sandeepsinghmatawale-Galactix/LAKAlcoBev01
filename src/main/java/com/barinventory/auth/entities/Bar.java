package com.barinventory.auth.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.barinventory.admin.enums.BarStatus;
import com.barinventory.inventory.entities.InventorySession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bars")
public class Bar {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long barId;

 @Column(nullable = false)
 private String barName;

 private String ownerName;
 private String phone;
 private String email;
 private String licenseNumber;
 private String address;
 private String city;
 private String state;
 private String pincode;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private BarStatus status = BarStatus.ACTIVE;

 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;

 @OneToMany(mappedBy = "bar")
 private List<InventorySession> sessions;
}