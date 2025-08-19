package com.coachera.backend.dto;

import com.coachera.backend.entity.Favorite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Favorite Data Transfer Object")
public class FavoriteDTO extends AuditableDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "Unique identifier of the favorite")
    private Integer id;

    @Schema(required = true, description = "Favorited course")
    private CourseDTO course;

    @Schema(required = true, description = "Student who favorited the course")
    private Integer studentId;

    // Constructor from entity
    public FavoriteDTO(Favorite favorite) {
        this.id = favorite.getId();
        this.course = new CourseDTO(favorite.getCourse());
        this.studentId = favorite.getStudent().getId();
        this.setCreatedAt(favorite.getCreatedAt());
        this.setUpdatedAt(favorite.getUpdatedAt());
    }
}
