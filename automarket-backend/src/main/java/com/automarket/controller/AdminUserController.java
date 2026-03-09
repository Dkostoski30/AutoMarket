package com.automarket.controller;

import com.automarket.dto.shared.PageResponse;
import com.automarket.dto.user.UserDto;
import com.automarket.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users (paginated)")
    public PageResponse<UserDto> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.listAll(page, size);
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Update a user's roles")
    public UserDto updateRoles(@PathVariable UUID id, @RequestBody Map<String, Set<String>> body) {
        return userService.updateRoles(id, body.get("roles"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete a user account")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
