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
        if (courses == null) {
            courses = new HashSet<>();
        }

        boolean exists = courses.stream()
                .anyMatch(ci -> ci.getCourse().equals(course));

        if (!exists) {
            CourseInstructor courseInstructor = new CourseInstructor(this, course);
            courses.add(courseInstructor);

            if (course.getInstructors() == null) {
                course.setInstructors(new HashSet<>());
            }
            course.getInstructors().add(courseInstructor);
        }
    }


    public void removeCourse(Course course) {
        if (courses == null || courses.isEmpty()) {
            return;
        }

        // find existing join entity
        CourseInstructor toRemove = courses.stream()
                .filter(ci -> ci.getCourse().equals(course))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            courses.remove(toRemove);

            if (course.getInstructors() != null) {
                course.getInstructors().remove(toRemove);
            }

            // break association explicitly if needed
            toRemove.setInstructor(null);
            toRemove.setCourse(null);
        }
    }


}