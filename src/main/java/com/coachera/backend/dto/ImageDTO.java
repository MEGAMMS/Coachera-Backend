package com.coachera.backend.dto;

import com.coachera.backend.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Image Data Transfer Object")
public class ImageDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "a1b2c3d4-e5f6-7890", 
           description = "Unique identifier for the image file")
    private String uuidName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "http://localhost:8080/images/a1b2c3d4-e5f6-7890",
           description = "Full URL to access the image")
    private String url;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.uuidName = image.getUuidName();
        this.url = image.getUrl();
        this.setCreatedAt(image.getCreatedAt());
        this.setUpdatedAt(image.getUpdatedAt());
    }

    // Static helper method to match the entity's functionality
    public static String extractUuidFromUrl(String url) {
        try {
            return url.substring(url.lastIndexOf("/") + 1);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid image URL format: " + url);
        }
    }
}