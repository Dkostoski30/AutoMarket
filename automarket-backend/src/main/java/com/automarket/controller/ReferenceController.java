package com.automarket.controller;

import com.automarket.dto.reference.ReferenceItemDto;
import com.automarket.service.ReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
@Tag(name = "Reference Data")
public class ReferenceController {

    private final ReferenceService referenceService;

    @GetMapping("/car-brands")
    @Operation(summary = "Get all car brands")
    public List<ReferenceItemDto> getBrands() { return referenceService.getBrands(); }

    @GetMapping("/fuel-types")
    @Operation(summary = "Get all fuel types")
    public List<ReferenceItemDto> getFuelTypes() { return referenceService.getFuelTypes(); }

    @GetMapping("/body-types")
    @Operation(summary = "Get all body types")
    public List<ReferenceItemDto> getBodyTypes() { return referenceService.getBodyTypes(); }

    @GetMapping("/condition-types")
    @Operation(summary = "Get all condition types")
    public List<ReferenceItemDto> getConditionTypes() { return referenceService.getConditionTypes(); }

    @GetMapping("/transmission-types")
    @Operation(summary = "Get all transmission types")
    public List<ReferenceItemDto> getTransmissionTypes() { return referenceService.getTransmissionTypes(); }

    @GetMapping("/cities")
    @Operation(summary = "Get all cities")
    public List<ReferenceItemDto> getCities() { return referenceService.getCities(); }
}
