package com.coachera.backend.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String durationHours;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal rating;

    @OneToOne
    @JoinColumn
    private Image image;

    // bidirectional relationship categories
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseCategory> categories = new HashSet<>();

    // bidirectional relationship with learning paths
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<LearningPathCourse> learningPaths = new HashSet<>();

    // bidirectional relationship with modules
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Module> modules = new HashSet<>();

    // Helper methods for managing categories
    public void addCategory(Category category) {
        CourseCategory courseCategory = new CourseCategory(this, category);
        categories.add(courseCategory);
        category.getCourses().add(courseCategory);
    }

    public void removeCategory(Category category) {
        CourseCategory courseCategory = new CourseCategory(this, category);
        category.getCourses().remove(courseCategory);
        categories.remove(courseCategory);
    }

    public void addModule(Module module) {
        if(modules ==null){
            modules =new HashSet<>();
        }
        modules.add(module);
        module.setCourse(this);
    }

    public void removeModule(Module module) {
        modules.remove(module);
        module.setCourse(null);
    }

}
