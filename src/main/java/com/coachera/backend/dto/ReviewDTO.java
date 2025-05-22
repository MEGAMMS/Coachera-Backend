package com.coachera.backend.dto;

import com.coachera.backend.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Review Data Transfer Object")
public class ReviewDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @Schema(example = "5", description = "Course ID being reviewed")
    private Integer courseId;

    @Schema(example = "10", description = "Student ID who wrote the review")
    private Integer studentId;

    @Schema(example = "4", description = "Rating (1-5)", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(example = "Great course with excellent content!", description = "Review comment")
    private String comment;

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.courseId = review.getCourse().getId();
        this.studentId = review.getStudent().getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.setCreatedAt(review.getCreatedAt());
        this.setUpdatedAt(review.getUpdatedAt());
    }
}