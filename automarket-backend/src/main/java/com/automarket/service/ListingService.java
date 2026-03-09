package com.automarket.service;

import com.automarket.config.CacheConfig;
import com.automarket.dto.listing.*;
import com.automarket.dto.shared.PageResponse;
import com.automarket.entity.*;
import com.automarket.exception.BusinessRuleException;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.*;
import com.automarket.specification.ListingSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final UserRepository userRepository;
    private final CarBrandRepository carBrandRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final BodyTypeRepository bodyTypeRepository;
    private final ConditionTypeRepository conditionTypeRepository;
    private final TransmissionTypeRepository transmissionTypeRepository;
    private final StorageService storageService;
    private final AnalyticsService analyticsService;

    @Value("${automarket.listings.max-per-free-user:3}")
    private int maxListingsPerFreeUser;

    @Value("${automarket.listings.max-images-per-listing:10}")
    private int maxImagesPerListing;

    @Transactional(readOnly = true)
    public PageResponse<ListingDto> browse(ListingFilterRequest filter, int page, int size) {
        Sort sort = buildSort(filter);
        Pageable pageable = PageRequest.of(page, size, sort);
        var spec = ListingSpecification.fromFilter(filter, true);
        Page<Listing> results = listingRepository.findAll(spec, pageable);
        return PageResponse.from(results, this::toListingDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.CACHE_LISTING_DETAIL, key = "#id")
    public ListingDetailDto getById(UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", id));
        analyticsService.recordView(listing);
        return toDetailDto(listing);
    }

    @Transactional(readOnly = true)
    public ListingDetailDto getBySlug(String slug) {
        Listing listing = listingRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Listing with slug: " + slug));
        analyticsService.recordView(listing);
        return toDetailDto(listing);
    }

    @Transactional(readOnly = true)
    public PageResponse<ListingDto> getMyListings(String email, int page, int size) {
        User user = getUserByEmail(email);
        Page<Listing> results = listingRepository.findBySeller(
                user, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return PageResponse.from(results, this::toListingDto);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.CACHE_FEATURED_LISTINGS, allEntries = true)
    public ListingDetailDto create(CreateListingRequest request, String sellerEmail) {
        User seller = getUserByEmail(sellerEmail);
        enforceListingLimit(seller);

        CarDetails carDetails = buildCarDetails(request);
        String slug = generateUniqueSlug(request.title() + "-" + request.model() + "-" + request.registrationYear());

        Listing listing = Listing.builder()
                .title(request.title())
                .slug(slug)
                .description(request.description())
                .price(request.price())
                .conditionType(getConditionType(request.conditionTypeId()))
                .seller(seller)
                .carDetails(carDetails)
                .approved(false)
                .build();

        listingRepository.save(listing);
        log.info("Listing created: {} by {}", listing.getId(), sellerEmail);
        return toDetailDto(listing);
    }

    @Transactional
    @CacheEvict(value = {CacheConfig.CACHE_LISTING_DETAIL, CacheConfig.CACHE_FEATURED_LISTINGS}, allEntries = true)
    public ListingDetailDto update(UUID id, UpdateListingRequest request, String editorEmail) {
        Listing listing = getListingAndCheckOwnership(id, editorEmail);

        if (request.title() != null) listing.setTitle(request.title());
        if (request.description() != null) listing.setDescription(request.description());
        if (request.price() != null) listing.setPrice(request.price());
        if (request.conditionTypeId() != null) listing.setConditionType(getConditionType(request.conditionTypeId()));

        CarDetails cd = listing.getCarDetails();
        if (request.brandId() != null) cd.setBrand(getBrand(request.brandId()));
        if (request.model() != null) cd.setModel(request.model());
        if (request.registrationYear() != null) cd.setRegistrationYear(request.registrationYear());
        if (request.kilometers() != null) cd.setKilometers(request.kilometers());
        if (request.fuelTypeId() != null) cd.setFuelType(getFuelType(request.fuelTypeId()));
        if (request.bodyTypeId() != null) cd.setBodyType(getBodyType(request.bodyTypeId()));
        if (request.transmissionTypeId() != null) cd.setTransmissionType(getTransmissionType(request.transmissionTypeId()));
        if (request.numDoors() != null) cd.setNumDoors(request.numDoors());
        if (request.numSeats() != null) cd.setNumSeats(request.numSeats());
        if (request.kilowatts() != null) cd.setKilowatts(request.kilowatts());

        // Reset approval on edit so moderator re-reviews
        listing.setApproved(false);

        listingRepository.save(listing);
        log.info("Listing updated: {} by {}", id, editorEmail);
        return toDetailDto(listing);
    }

    @Transactional
    @CacheEvict(value = {CacheConfig.CACHE_LISTING_DETAIL, CacheConfig.CACHE_FEATURED_LISTINGS}, allEntries = true)
    public void delete(UUID id, String deleterEmail) {
        Listing listing = getListingAndCheckOwnership(id, deleterEmail);
        listing.softDelete();
        listingRepository.save(listing);
        log.info("Listing soft-deleted: {} by {}", id, deleterEmail);
    }

    @Transactional
    public ListingDetailDto addImage(UUID listingId, MultipartFile file, String ownerEmail) {
        Listing listing = getListingAndCheckOwnership(listingId, ownerEmail);

        int currentCount = listingImageRepository.countByListing(listing);
        if (currentCount >= maxImagesPerListing) {
            throw new BusinessRuleException("Maximum " + maxImagesPerListing + " images per listing");
        }

        StorageService.UploadResult result = storageService.store(file, "listings/" + listingId);
        ListingImage image = ListingImage.builder()
                .listing(listing)
                .storageKey(result.storageKey())
                .url(result.url())
                .displayOrder(currentCount)
                .build();

        listingImageRepository.save(image);
        log.info("Image added to listing {}: {}", listingId, result.storageKey());
        return toDetailDto(listing);
    }

    @Transactional
    @CacheEvict(value = CacheConfig.CACHE_LISTING_DETAIL, key = "#listingId")
    public void deleteImage(UUID listingId, UUID imageId, String ownerEmail) {
        getListingAndCheckOwnership(listingId, ownerEmail);
        ListingImage image = listingImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", imageId));

        storageService.delete(image.getStorageKey());
        listingImageRepository.delete(image);
        log.info("Image {} deleted from listing {}", imageId, listingId);
    }

    @Transactional(readOnly = true)
    @Cacheable(CacheConfig.CACHE_FEATURED_LISTINGS)
    public List<ListingDto> getFeatured() {
        return listingRepository.findActiveFeatured().stream()
                .map(this::toListingDto)
                .toList();
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void enforceListingLimit(User seller) {
        if (seller.getPlan() == User.Plan.FREE) {
            long activeCount = listingRepository.countActiveByUser(seller);
            if (activeCount >= maxListingsPerFreeUser) {
                throw new BusinessRuleException(
                        "Free plan allows maximum " + maxListingsPerFreeUser +
                        " active listings. Upgrade to Premium for unlimited listings.");
            }
        }
    }

    private Listing getListingAndCheckOwnership(UUID listingId, String requesterEmail) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", listingId));

        User requester = getUserByEmail(requesterEmail);
        boolean isOwner = listing.getSeller().getId().equals(requester.getId());
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(r -> r.getName() == Role.RoleName.ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You do not own this listing");
        }
        return listing;
    }

    private CarDetails buildCarDetails(CreateListingRequest r) {
        return CarDetails.builder()
                .brand(getBrand(r.brandId()))
                .model(r.model())
                .registrationYear(r.registrationYear())
                .kilometers(r.kilometers())
                .fuelType(getFuelType(r.fuelTypeId()))
                .bodyType(getBodyType(r.bodyTypeId()))
                .transmissionType(getTransmissionType(r.transmissionTypeId()))
                .numDoors(r.numDoors())
                .numSeats(r.numSeats())
                .kilowatts(r.kilowatts())
                .build();
    }

    private Sort buildSort(ListingFilterRequest filter) {
        if (filter == null || filter.sortBy() == null) {
            return Sort.by("createdAt").descending();
        }
        Sort.Direction dir = "asc".equalsIgnoreCase(filter.sortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return switch (filter.sortBy()) {
            case "price" -> Sort.by(dir, "price");
            case "year"  -> Sort.by(dir, "carDetails.registrationYear");
            default      -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private String generateUniqueSlug(String title) {
        String base = slugify(title);
        String slug = base;
        int suffix = 1;
        while (listingRepository.existsBySlug(slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return NON_LATIN.matcher(WHITESPACE.matcher(normalized.toLowerCase(Locale.ENGLISH)).replaceAll("-"))
                .replaceAll("")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private CarBrand getBrand(UUID id) {
        return carBrandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CarBrand", id));
    }
    private FuelType getFuelType(UUID id) {
        return fuelTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FuelType", id));
    }
    private BodyType getBodyType(UUID id) {
        return bodyTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("BodyType", id));
    }
    private ConditionType getConditionType(UUID id) {
        return conditionTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ConditionType", id));
    }
    private TransmissionType getTransmissionType(UUID id) {
        return transmissionTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TransmissionType", id));
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    public ListingDto toListingDtoPublic(Listing l) { return toListingDto(l); }

    private ListingDto toListingDto(Listing l) {
        CarDetails cd = l.getCarDetails();
        String thumbnail = l.getImages().isEmpty() ? null : l.getImages().get(0).getUrl();
        return new ListingDto(
                l.getId(), l.getSlug(), l.getTitle(), l.getPrice(),
                l.isFeaturedActive(), l.isApproved(), l.getCreatedAt(), thumbnail,
                new ListingDto.CarDetailsDto(
                        cd.getBrand() != null ? cd.getBrand().getName() : null,
                        cd.getModel(), cd.getRegistrationYear(), cd.getKilometers(),
                        cd.getFuelType() != null ? cd.getFuelType().getName() : null,
                        cd.getBodyType() != null ? cd.getBodyType().getName() : null,
                        cd.getTransmissionType() != null ? cd.getTransmissionType().getName() : null,
                        cd.getKilowatts()
                ),
                new ListingDto.SellerSummaryDto(
                        l.getSeller().getId(), l.getSeller().getName(),
                        l.getSeller().getCity() != null ? l.getSeller().getCity().getName() : null
                )
        );
    }

    public ListingDetailDto toDetailDtoPublic(Listing l) { return toDetailDto(l); }

    private ListingDetailDto toDetailDto(Listing l) {
        CarDetails cd = l.getCarDetails();
        List<ListingDetailDto.ImageDto> images = l.getImages().stream()
                .map(img -> new ListingDetailDto.ImageDto(img.getId(), img.getUrl(), img.getDisplayOrder()))
                .toList();

        return new ListingDetailDto(
                l.getId(), l.getSlug(), l.getTitle(), l.getDescription(), l.getPrice(),
                l.isFeaturedActive(), l.isApproved(), l.getCreatedAt(), images,
                new ListingDetailDto.CarDetailsFullDto(
                        cd.getBrand() != null ? cd.getBrand().getId() : null,
                        cd.getBrand() != null ? cd.getBrand().getName() : null,
                        cd.getModel(), cd.getRegistrationYear(), cd.getKilometers(),
                        cd.getFuelType() != null ? cd.getFuelType().getId() : null,
                        cd.getFuelType() != null ? cd.getFuelType().getName() : null,
                        cd.getBodyType() != null ? cd.getBodyType().getId() : null,
                        cd.getBodyType() != null ? cd.getBodyType().getName() : null,
                        cd.getTransmissionType() != null ? cd.getTransmissionType().getId() : null,
                        cd.getTransmissionType() != null ? cd.getTransmissionType().getName() : null,
                        cd.getNumDoors(), cd.getNumSeats(), cd.getKilowatts()
                ),
                new ListingDetailDto.SellerDetailDto(
                        l.getSeller().getId(), l.getSeller().getName(), l.getSeller().getPhone(),
                        l.getSeller().getCity() != null ? l.getSeller().getCity().getName() : null,
                        l.getSeller().getCreatedAt()
                ),
                l.getConditionType() != null
                        ? new ListingDetailDto.ConditionDto(l.getConditionType().getId(), l.getConditionType().getName())
                        : null
        );
    }
}
