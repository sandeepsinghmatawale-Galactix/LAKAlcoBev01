package com.barinventory.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor   // ✅ REQUIRED for JPA
public class BarUser {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    // ✅ bar mapping (keep simple for now)
    private Long barId;
}