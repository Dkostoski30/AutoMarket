package com.automarket.repository;

import com.automarket.entity.Inquiry;
import com.automarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InquiryRepository extends JpaRepository<Inquiry, UUID> {
    Page<Inquiry> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);
    Page<Inquiry> findByListing_SellerOrderByCreatedAtDesc(User seller, Pageable pageable);
    long countByListing_SellerAndReadBySellerFalse(User seller);
}
