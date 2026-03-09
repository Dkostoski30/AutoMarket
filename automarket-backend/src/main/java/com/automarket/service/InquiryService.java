package com.automarket.service;

import com.automarket.dto.inquiry.InquiryDto;
import com.automarket.dto.inquiry.SendInquiryRequest;
import com.automarket.dto.shared.PageResponse;
import com.automarket.entity.Inquiry;
import com.automarket.entity.Listing;
import com.automarket.entity.User;
import com.automarket.exception.BusinessRuleException;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.InquiryRepository;
import com.automarket.repository.ListingRepository;
import com.automarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public InquiryDto send(SendInquiryRequest request, String senderEmail) {
        User sender = getUserOrThrow(senderEmail);
        Listing listing = listingRepository.findById(request.listingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing", request.listingId()));

        if (!listing.isApproved()) {
            throw new BusinessRuleException("Cannot inquire about an unapproved listing");
        }
        if (listing.getSeller().getEmail().equals(senderEmail)) {
            throw new BusinessRuleException("Cannot inquire about your own listing");
        }

        Inquiry inquiry = inquiryRepository.save(Inquiry.builder()
                .listing(listing)
                .sender(sender)
                .message(request.message())
                .build());

        emailService.sendNewInquiry(listing.getSeller().getEmail(), listing.getTitle(), sender.getName());
        log.info("Inquiry sent: {} -> listing {}", senderEmail, listing.getId());
        return toDto(inquiry);
    }

    @Transactional(readOnly = true)
    public PageResponse<InquiryDto> getReceived(String sellerEmail, int page, int size) {
        User seller = getUserOrThrow(sellerEmail);
        return PageResponse.from(
                inquiryRepository.findByListing_SellerOrderByCreatedAtDesc(seller, PageRequest.of(page, size)),
                this::toDto);
    }

    @Transactional(readOnly = true)
    public PageResponse<InquiryDto> getSent(String senderEmail, int page, int size) {
        User sender = getUserOrThrow(senderEmail);
        return PageResponse.from(
                inquiryRepository.findBySenderOrderByCreatedAtDesc(sender, PageRequest.of(page, size)),
                this::toDto);
    }

    @Transactional
    public void markAsRead(UUID inquiryId, String sellerEmail) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", inquiryId));
        if (!inquiry.getListing().getSeller().getEmail().equals(sellerEmail)) {
            throw new BusinessRuleException("You are not the seller for this inquiry");
        }
        inquiry.setReadBySeller(true);
        inquiryRepository.save(inquiry);
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private InquiryDto toDto(Inquiry i) {
        return new InquiryDto(
                i.getId(),
                i.getListing().getId(),
                i.getListing().getTitle(),
                i.getSender().getId(),
                i.getSender().getName(),
                i.getMessage(),
                i.isReadBySeller(),
                i.getCreatedAt()
        );
    }
}
