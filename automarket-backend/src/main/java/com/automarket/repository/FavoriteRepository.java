package com.automarket.repository;

import com.automarket.entity.Favorite;
import com.automarket.entity.Listing;
import com.automarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    Optional<Favorite> findByUserAndListing(User user, Listing listing);
    boolean existsByUserAndListing(User user, Listing listing);
    Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    void deleteByUserAndListing(User user, Listing listing);
}
