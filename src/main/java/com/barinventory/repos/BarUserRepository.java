package com.barinventory.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barinventory.entities.BarUser;

@Repository
public interface BarUserRepository extends JpaRepository<BarUser, Long> {

    Optional<BarUser> findByUsername(String username);
}