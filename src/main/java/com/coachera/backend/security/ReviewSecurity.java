package com.coachera.backend.security;

import com.coachera.backend.entity.User;
import com.coachera.backend.repository.ReviewRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;

    public ReviewSecurity(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public boolean canModifyReview(Integer reviewId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return reviewRepository.existsByIdAndStudentUserId(reviewId, user.getId());
    }
}