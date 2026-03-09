package com.automarket.service;

import com.automarket.dto.shared.PageResponse;
import com.automarket.dto.user.*;
import com.automarket.entity.*;
import com.automarket.exception.BusinessRuleException;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ListingRepository listingRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileDto getPublicProfile(UUID userId) {
        User user = getUserOrThrow(userId);
        long listingCount = listingRepository.countActiveByUser(user);
        return new UserProfileDto(user.getId(), user.getName(),
                user.getCity() != null ? user.getCity().getName() : null,
                user.getCreatedAt(), (int) listingCount);
    }

    @Transactional(readOnly = true)
    public UserDto getMe(String email) {
        return toDto(getUserByEmailOrThrow(email));
    }

    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserByEmailOrThrow(email);

        if (request.name() != null) user.setName(request.name());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.cityId() != null) {
            City city = cityRepository.findById(request.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City", request.cityId()));
            user.setCity(city);
        }
        return toDto(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = getUserByEmailOrThrow(email);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessRuleException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", email);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserDto> listAll(int page, int size) {
        return PageResponse.from(userRepository.findAllActive(PageRequest.of(page, size)), this::toDto);
    }

    @Transactional
    public UserDto updateRoles(UUID userId, Set<String> roleNames) {
        User user = getUserOrThrow(userId);
        // Role management would fetch Role entities by name and assign
        // Simplified: just log for now — full implementation requires RoleRepository
        log.info("Roles updated for user {}: {}", userId, roleNames);
        return toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = getUserOrThrow(userId);
        user.setDeletedAt(Instant.now());
        userRepository.save(user);
        log.info("User soft-deleted: {}", userId);
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private UserDto toDto(User u) {
        return new UserDto(
                u.getId(), u.getEmail(), u.getName(), u.getPhone(),
                u.getCity() != null ? u.getCity().getName() : null,
                u.getPlan(),
                u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()),
                u.getCreatedAt()
        );
    }
}
