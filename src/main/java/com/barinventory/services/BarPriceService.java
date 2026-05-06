package com.barinventory.services;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.barinventory.repos.BarPriceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarPriceService {

    private final BarPriceRepository barPriceRepo;

     
    public Map<Long, Double> getPriceMap(Long barId) {

        return barPriceRepo.findByBarBarId(barId)
                .stream()
                .collect(Collectors.toMap(
                        bp -> bp.getBrand().getBrandId(),
                        bp -> bp.getPrice()
                ));
    }
}