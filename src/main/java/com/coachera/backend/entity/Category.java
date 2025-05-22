package com.coachera.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // Add bidirectional relationship
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseCategory> courses = new HashSet<>();

    // Helper methods for managing courses
    public void addCourse(Course course) {
        CourseCategory courseCategory = new CourseCategory(course, this);
        if (courses == null) {
            courses = new HashSet<>();
        }
        courses.add(courseCategory);
        course.getCategories().add(courseCategory);
    }

    public void removeCourse(Course course) {
        CourseCategory courseCategory = new CourseCategory(course, this);
        course.getCategories().remove(courseCategory);
        courses.remove(courseCategory);
    }
}
