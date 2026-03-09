package com.automarket.specification;

import com.automarket.dto.listing.ListingFilterRequest;
import com.automarket.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds dynamic JPA {@link Specification} predicates from a {@link ListingFilterRequest}.
 * All filters are optional and composed with AND.
 * This replaces the in-memory string-comparison filtering from the legacy .NET app.
 */
public final class ListingSpecification {

    private ListingSpecification() {}

    public static Specification<Listing> fromFilter(ListingFilterRequest filter, boolean approvedOnly) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter soft-deleted
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (approvedOnly) {
                predicates.add(cb.isTrue(root.get("approved")));
            }

            if (filter == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            // Full-text / keyword search on title and description
            if (StringUtils.hasText(filter.search())) {
                String pattern = "%" + filter.search().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("carDetails").get("model")), pattern)
                ));
            }

            // Price range
            if (filter.priceFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.priceFrom()));
            }
            if (filter.priceTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.priceTo()));
            }

            // Year range
            if (filter.yearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("carDetails").get("registrationYear"), filter.yearFrom()));
            }
            if (filter.yearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("carDetails").get("registrationYear"), filter.yearTo()));
            }

            // Kilometer range
            if (filter.kilometersFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("carDetails").get("kilometers"), filter.kilometersFrom()));
            }
            if (filter.kilometersTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("carDetails").get("kilometers"), filter.kilometersTo()));
            }

            // Kilowatt range
            if (filter.kilowattsFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("carDetails").get("kilowatts"), filter.kilowattsFrom()));
            }
            if (filter.kilowattsTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("carDetails").get("kilowatts"), filter.kilowattsTo()));
            }

            // Multi-value filters (IN clauses)
            if (!CollectionUtils.isEmpty(filter.fuelTypeIds())) {
                predicates.add(root.get("carDetails").get("fuelType").get("id").in(filter.fuelTypeIds()));
            }
            if (!CollectionUtils.isEmpty(filter.bodyTypeIds())) {
                predicates.add(root.get("carDetails").get("bodyType").get("id").in(filter.bodyTypeIds()));
            }
            if (!CollectionUtils.isEmpty(filter.conditionTypeIds())) {
                predicates.add(root.get("conditionType").get("id").in(filter.conditionTypeIds()));
            }
            if (!CollectionUtils.isEmpty(filter.transmissionTypeIds())) {
                predicates.add(root.get("carDetails").get("transmissionType").get("id").in(filter.transmissionTypeIds()));
            }
            if (!CollectionUtils.isEmpty(filter.brandIds())) {
                predicates.add(root.get("carDetails").get("brand").get("id").in(filter.brandIds()));
            }

            // City filter (browse by city = seller's city)
            if (filter.cityId() != null) {
                predicates.add(cb.equal(root.get("seller").get("city").get("id"), filter.cityId()));
            }

            // Featured filter
            if (Boolean.TRUE.equals(filter.featured())) {
                predicates.add(cb.isTrue(root.get("featured")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
