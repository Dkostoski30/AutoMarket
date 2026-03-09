package com.automarket.controller;

import com.automarket.dto.reference.ReferenceItemDto;
import com.automarket.service.ReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reference")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Reference Data")
public class AdminReferenceController {

    private final ReferenceService referenceService;

    @PostMapping("/car-brands")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new car brand")
    public ReferenceItemDto createBrand(@RequestBody Map<String, String> body) {
        return referenceService.createBrand(body.get("name"));
    }

    @DeleteMapping("/car-brands/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a car brand")
    public void deleteBrand(@PathVariable UUID id) {
        referenceService.deleteBrand(id);
    }
}
