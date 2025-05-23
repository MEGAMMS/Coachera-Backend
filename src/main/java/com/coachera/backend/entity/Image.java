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
public class Image extends Auditable{
	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String uuidName;

	public String getUrl() {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/images/")
				.path(this.uuidName)
				.toUriString();
	}

	public static String extractUuidFromUrl(String url) {
		try {
			return url.substring(url.lastIndexOf("/") + 1);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid image URL format: " + url);
		}
	}
}
