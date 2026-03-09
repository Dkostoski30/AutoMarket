package com.automarket.repository;

import com.automarket.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FuelTypeRepository extends JpaRepository<FuelType, UUID> {
    List<FuelType> findAllByOrderByNameAsc();
}
