package com.automarket.controller;

import com.automarket.dto.user.*;
import com.automarket.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a user's public profile")
    public UserProfileDto getPublicProfile(@PathVariable UUID id) {
        return userService.getPublicProfile(id);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user's full profile")
    public UserDto getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getMe(userDetails.getUsername());
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update current user's profile")
    public UserDto updateMe(@Valid @RequestBody UpdateProfileRequest request,
                            @AuthenticationPrincipal UserDetails userDetails) {
        return userService.updateProfile(userDetails.getUsername(), request);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change current user's password")
    public void changePassword(@RequestBody Map<String, String> body,
                               @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(
                userDetails.getUsername(),
                body.get("currentPassword"),
                body.get("newPassword")
        );
    }
}
