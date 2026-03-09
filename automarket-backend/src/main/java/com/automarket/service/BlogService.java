package com.automarket.service;

import com.automarket.dto.blog.BlogDto;
import com.automarket.dto.blog.BlogRequest;
import com.automarket.dto.shared.PageResponse;
import com.automarket.entity.Blog;
import com.automarket.entity.User;
import com.automarket.exception.ResourceNotFoundException;
import com.automarket.repository.BlogRepository;
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
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<BlogDto> list(int page, int size) {
        return PageResponse.from(
                blogRepository.findAllOrderByCreatedAtDesc(PageRequest.of(page, size)),
                this::toDto
        );
    }

    @Transactional(readOnly = true)
    public BlogDto getById(UUID id) {
        return toDto(blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", id)));
    }

    @Transactional
    public BlogDto create(BlogRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", authorEmail));

        Blog blog = Blog.builder()
                .title(request.title())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .author(author)
                .build();

        blogRepository.save(blog);
        log.info("Blog created: {} by {}", blog.getId(), authorEmail);
        return toDto(blog);
    }

    @Transactional
    public BlogDto update(UUID id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog", id));

        blog.setTitle(request.title());
        blog.setContent(request.content());
        blog.setImageUrl(request.imageUrl());
        return toDto(blogRepository.save(blog));
    }

    @Transactional
    public void delete(UUID id) {
        if (!blogRepository.existsById(id)) throw new ResourceNotFoundException("Blog", id);
        blogRepository.deleteById(id);
        log.info("Blog deleted: {}", id);
    }

    private BlogDto toDto(Blog b) {
        return new BlogDto(
                b.getId(), b.getTitle(), b.getContent(), b.getImageUrl(),
                b.getAuthor() != null ? b.getAuthor().getName() : null,
                b.getCreatedAt()
        );
    }
}
