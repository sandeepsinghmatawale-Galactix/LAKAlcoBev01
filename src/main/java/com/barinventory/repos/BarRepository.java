package com.barinventory.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barinventory.entities.Bar;

public interface BarRepository extends JpaRepository<Bar, Long> {

}
