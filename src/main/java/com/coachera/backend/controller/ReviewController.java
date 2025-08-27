package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.ReviewDTO;
import com.coachera.backend.dto.ReviewRequestDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ReviewDTO> createReview(
            @RequestBody @Valid ReviewRequestDTO reviewDTO,
            @AuthenticationPrincipal User user) {
        ReviewDTO createdReview = reviewService.createReview(reviewDTO,user);
        return ApiResponse.created("Review created successfully", createdReview);
    }

    @GetMapping("/{id}")
    public ApiResponse<ReviewDTO> getReviewById(@PathVariable Integer id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return ApiResponse.success(review);
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<ReviewDTO>> getReviewsByCourse(@PathVariable Integer courseId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByCourse(courseId);
        return ApiResponse.success(reviews);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<ReviewDTO>> getReviewsByStudent(@AuthenticationPrincipal User user) {
        List<ReviewDTO> reviews = reviewService.getReviewsByStudent(user);
        return ApiResponse.success(reviews);
    }

    @GetMapping("/course/{courseId}/average-rating")
    public ApiResponse<Double> getAverageRatingForCourse(@PathVariable Integer courseId) {
        double averageRating = reviewService.getAverageRatingForCourse(courseId);
        return ApiResponse.success(averageRating);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@reviewSecurity.canModifyReview(#id, authentication)")
    public ApiResponse<ReviewDTO> updateReview(
            @PathVariable Integer id,
            @RequestBody @Valid ReviewRequestDTO reviewDTO,
            @AuthenticationPrincipal User user) {
        ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO,user);
        return ApiResponse.success("Review updated successfully", updatedReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@reviewSecurity.canModifyReview(#id, authentication)")
    public ApiResponse<Void> deleteReview(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id,user);
        return ApiResponse.noContentResponse();
    }
}