package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.ReviewDTO;
import com.coachera.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ReviewDTO> createReview(@RequestBody @Valid ReviewDTO reviewDTO) {
        ReviewDTO createdReview = reviewService.createReview(reviewDTO);
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

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') and #studentId == principal.id")
    public ApiResponse<List<ReviewDTO>> getReviewsByStudent(@PathVariable Integer studentId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByStudent(studentId);
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
            @RequestBody @Valid ReviewDTO reviewDTO) {
        ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
        return ApiResponse.success("Review updated successfully", updatedReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@reviewSecurity.canModifyReview(#id, authentication)")
    public ApiResponse<Void> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ApiResponse.noContentResponse();
    }
}