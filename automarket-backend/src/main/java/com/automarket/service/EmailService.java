package com.automarket.service;

import com.automarket.entity.Listing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${automarket.mail.from:noreply@automarket.com}")
    private String fromAddress;

    @Value("${automarket.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    @Async
    public void sendListingApproved(Listing listing) {
        String sellerEmail = listing.getSeller().getEmail();
        sendSimple(
                sellerEmail,
                "Your listing has been approved — AutoMarket",
                "Great news! Your listing \"" + listing.getTitle() + "\" has been approved and is now live.\n\n" +
                "View it at: " + frontendBaseUrl + "/listings/" + listing.getSlug()
        );
    }

    @Async
    public void sendListingRejected(Listing listing, String reason) {
        String sellerEmail = listing.getSeller().getEmail();
        sendSimple(
                sellerEmail,
                "Your listing requires changes — AutoMarket",
                "Your listing \"" + listing.getTitle() + "\" was not approved.\n\n" +
                "Reason: " + (reason != null ? reason : "Does not meet our guidelines.") + "\n\n" +
                "Please review our posting guidelines and resubmit."
        );
    }

    @Async
    public void sendNewInquiry(String sellerEmail, String listingTitle, String senderName) {
        sendSimple(
                sellerEmail,
                "New inquiry on your listing — AutoMarket",
                senderName + " sent you a message about your listing \"" + listingTitle + "\".\n\n" +
                "Log in to AutoMarket to view and respond: " + frontendBaseUrl + "/inquiries"
        );
    }

    private void sendSimple(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.debug("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
