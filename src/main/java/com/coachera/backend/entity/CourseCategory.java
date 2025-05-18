package com.coachera.backend.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories_course")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseCategory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Composite key constructor
    public CourseCategory(Course course, Category category) {
        this.course = course;
        this.category = category;
    }

    // Override equals and hashCode based on course and category
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseCategory)) return false;
        CourseCategory that = (CourseCategory) o;
        return Objects.equals(course.getId(), that.course.getId()) &&
               Objects.equals(category.getId(), that.category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(course.getId(), category.getId());
    }
}

