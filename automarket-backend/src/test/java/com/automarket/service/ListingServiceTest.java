package com.automarket.service;

import com.automarket.dto.listing.CreateListingRequest;
import com.automarket.entity.*;
import com.automarket.exception.BusinessRuleException;
import com.automarket.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListingService")
class ListingServiceTest {

    @Mock ListingRepository listingRepository;
    @Mock ListingImageRepository listingImageRepository;
    @Mock UserRepository userRepository;
    @Mock CarBrandRepository carBrandRepository;
    @Mock FuelTypeRepository fuelTypeRepository;
    @Mock BodyTypeRepository bodyTypeRepository;
    @Mock ConditionTypeRepository conditionTypeRepository;
    @Mock TransmissionTypeRepository transmissionTypeRepository;
    @Mock StorageService storageService;
    @Mock AnalyticsService analyticsService;

    @InjectMocks
    ListingService listingService;

    private User freeUser;
    private UUID brandId, fuelTypeId, bodyTypeId, conditionTypeId, transmissionTypeId;

    @BeforeEach
    void setUp() {
        freeUser = User.builder()
                .id(UUID.randomUUID())
                .email("seller@test.com")
                .name("Test Seller")
                .plan(User.Plan.FREE)
                .build();

        brandId        = UUID.randomUUID();
        fuelTypeId     = UUID.randomUUID();
        bodyTypeId     = UUID.randomUUID();
        conditionTypeId = UUID.randomUUID();
        transmissionTypeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("create: throws BusinessRuleException when FREE user exceeds listing limit")
    void create_throwsWhenFreeUserExceedsLimit() {
        when(userRepository.findByEmail("seller@test.com")).thenReturn(Optional.of(freeUser));
        when(listingRepository.countActiveByUser(freeUser)).thenReturn(3L);

        CreateListingRequest request = buildRequest();

        assertThatThrownBy(() -> listingService.create(request, "seller@test.com"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Free plan");
    }

    @Test
    @DisplayName("create: PREMIUM user can create listing beyond free limit")
    void create_premiumUserCanExceedFreeLimit() {
        freeUser.setPlan(User.Plan.PREMIUM);
        when(userRepository.findByEmail("seller@test.com")).thenReturn(Optional.of(freeUser));

        stubReferenceData();
        when(listingRepository.existsBySlug(anyString())).thenReturn(false);
        when(listingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Should not throw
        assertThatNoException().isThrownBy(() -> listingService.create(buildRequest(), "seller@test.com"));
    }

    private CreateListingRequest buildRequest() {
        return new CreateListingRequest(
                "2022 BMW 3 Series", "Well-maintained vehicle in excellent condition",
                new BigDecimal("25000"), conditionTypeId, brandId, "320d",
                2022, 50000, fuelTypeId, bodyTypeId, transmissionTypeId,
                4, 5, 140
        );
    }

    private void stubReferenceData() {
        var brand = CarBrand.builder().id(brandId).name("BMW").build();
        var fuel  = FuelType.builder().id(fuelTypeId).name("Diesel").build();
        var body  = BodyType.builder().id(bodyTypeId).name("Sedan").build();
        var cond  = ConditionType.builder().id(conditionTypeId).name("Used").build();
        var trans = TransmissionType.builder().id(transmissionTypeId).name("Automatic").build();

        when(carBrandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(fuelTypeRepository.findById(fuelTypeId)).thenReturn(Optional.of(fuel));
        when(bodyTypeRepository.findById(bodyTypeId)).thenReturn(Optional.of(body));
        when(conditionTypeRepository.findById(conditionTypeId)).thenReturn(Optional.of(cond));
        when(transmissionTypeRepository.findById(transmissionTypeId)).thenReturn(Optional.of(trans));
    }
}
