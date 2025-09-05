package com.coachera.backend.entity;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video extends Auditable {
	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String uuidName;

	@Column(name = "external_url")
	private String externalUrl;

	public String getUrl() {
		// If it's an external URL, return it directly
		if (this.externalUrl != null && !this.externalUrl.isEmpty()) {
			return this.externalUrl;
		}
		
		// Otherwise, return the uploaded file URL
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/videos/")
				.path(this.uuidName)
				.toUriString();
	}

	public static String extractUuidFromUrl(String url) {
		try {
			return url.substring(url.lastIndexOf("/") + 1);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid video URL format: " + url);
		}
	}
}
