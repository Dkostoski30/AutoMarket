package com.automarket.service;

import com.automarket.config.CacheConfig;
import com.automarket.dto.reference.ReferenceItemDto;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final CarBrandRepository carBrandRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final BodyTypeRepository bodyTypeRepository;
    private final ConditionTypeRepository conditionTypeRepository;
    private final TransmissionTypeRepository transmissionTypeRepository;
    private final CityRepository cityRepository;

    @Cacheable(CacheConfig.CACHE_REFERENCE_DATA + "::brands")
    @Transactional(readOnly = true)
    public List<ReferenceItemDto> getBrands() {
        return carBrandRepository.findAllByOrderByNameAsc().stream()
                .map(b -> new ReferenceItemDto(b.getId(), b.getName())).toList();
    }

    @Cacheable(CacheConfig.CACHE_REFERENCE_DATA + "::fuel-types")
    @Transactional(readOnly = true)
    public List<ReferenceItemDto> getFuelTypes() {
        return fuelTypeRepository.findAllByOrderByNameAsc().stream()
                .map(b -> new ReferenceItemDto(b.getId(), b.getName())).toList();
    }

    @Cacheable(CacheConfig.CACHE_REFERENCE_DATA + "::body-types")
    @Transactional(readOnly = true)
    public List<ReferenceItemDto> getBodyTypes() {
        return bodyTypeRepository.findAllByOrderByNameAsc().stream()
                .map(b -> new ReferenceItemDto(b.getId(), b.getName())).toList();
    }

    @Cacheable(CacheConfig.CACHE_REFERENCE_DATA + "::condition-types")
    @Transactional(readOnly = true)
    public List<ReferenceItemDto> getConditionTypes() {
        return conditionTypeRepository.findAllByOrderByNameAsc().stream()
                .map(b -> new ReferenceItemDto(b.getId(), b.getName())).toList();
    }

    @Cacheable(CacheConfig.CACHE_REFERENCE_DATA + "::transmission-types")
    @Transactional(readOnly = true)
    public List<ReferenceItemDto> getTransmissionTypes() {
        return transmissionTypeRepository.findAllByOrderByNameAsc().stream()
                .map(b -> new ReferenceItemDto(b.getId(), b.getName())).toList();
    }

    @Cacheable(CacheConfig.CACHE_REFERENCE_DATA + "::cities")
    @Transactional(readOnly = true)
    public List<ReferenceItemDto> getCities() {
        return cityRepository.findAllByOrderByNameAsc().stream()
                .map(c -> new ReferenceItemDto(c.getId(), c.getName())).toList();
    }

    @CacheEvict(value = CacheConfig.CACHE_REFERENCE_DATA, allEntries = true)
    @Transactional
    public ReferenceItemDto createBrand(String name) {
        if (carBrandRepository.existsByNameIgnoreCase(name)) {
            throw new com.automarket.exception.BusinessRuleException("Brand already exists: " + name);
        }
        var brand = carBrandRepository.save(
                com.automarket.entity.CarBrand.builder().name(name).build());
        return new ReferenceItemDto(brand.getId(), brand.getName());
    }

    @CacheEvict(value = CacheConfig.CACHE_REFERENCE_DATA, allEntries = true)
    @Transactional
    public void deleteBrand(UUID id) {
        if (!carBrandRepository.existsById(id)) throw new ResourceNotFoundException("CarBrand", id);
        carBrandRepository.deleteById(id);
    }
}
