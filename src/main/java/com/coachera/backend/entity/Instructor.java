package com.coachera.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instructors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseInstructor> courses = new HashSet<>();

    // Helper method to maintain bidirectional relationship
    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getInstructor() != this) {
            user.setInstructor(this);
        }
    }

     // Helper methods
    public void addCourse(Course course) {
        CourseInstructor courseInstructor = new CourseInstructor(this, course);
        courses.add(courseInstructor);
        course.getInstructors().add(courseInstructor);
    }

    public void removeCourse(Course course) {
        CourseInstructor courseInstructor = new CourseInstructor(this, course);
        courses.remove(courseInstructor);
        course.getInstructors().remove(courseInstructor);
        courseInstructor.setInstructor(null);
        courseInstructor.setCourse(null);
    }

}