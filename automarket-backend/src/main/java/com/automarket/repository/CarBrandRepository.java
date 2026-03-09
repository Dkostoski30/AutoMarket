package com.automarket.repository;

import com.automarket.entity.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CarBrandRepository extends JpaRepository<CarBrand, UUID> {
    List<CarBrand> findAllByOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
