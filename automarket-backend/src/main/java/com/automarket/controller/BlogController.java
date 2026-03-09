package com.automarket.controller;

import com.automarket.dto.blog.*;
import com.automarket.dto.shared.PageResponse;
import com.automarket.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
@Tag(name = "Blog")
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    @Operation(summary = "List blog posts (paginated)")
    public PageResponse<BlogDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return blogService.list(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a blog post by ID")
    public BlogDto getById(@PathVariable UUID id) {
        return blogService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Create a blog post")
    public BlogDto create(@Valid @RequestBody BlogRequest request,
                          @AuthenticationPrincipal UserDetails userDetails) {
        return blogService.create(request, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Update a blog post")
    public BlogDto update(@PathVariable UUID id, @Valid @RequestBody BlogRequest request) {
        return blogService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a blog post")
    public void delete(@PathVariable UUID id) {
        blogService.delete(id);
    }
}
