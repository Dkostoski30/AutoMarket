package com.automarket.repository;

import com.automarket.entity.TransmissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransmissionTypeRepository extends JpaRepository<TransmissionType, UUID> {
    List<TransmissionType> findAllByOrderByNameAsc();
}
