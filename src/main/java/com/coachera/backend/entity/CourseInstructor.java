package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_instructors")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseInstructor extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public CourseInstructor(Instructor instructor, Course course) {
        this.instructor = instructor;
        this.course = course;
    }

    

    // Equals and hashCode implementations to prevent duplicate entries
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseInstructor)) return false;
        CourseInstructor that = (CourseInstructor) o;
        return instructor != null && instructor.getId() != null && 
               course != null && course.getId() != null &&
               instructor.getId().equals(that.instructor.getId()) && 
               course.getId().equals(that.course.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Bidirectional relationship management methods
    public void setInstructor(Instructor instructor) {
        if (this.instructor != null) {
            this.instructor.getCourses().remove(this);
        }
        this.instructor = instructor;
        if (instructor != null) {
            instructor.getCourses().add(this);
        }
    }

    public void setCourse(Course course) {
        if (this.course != null) {
            this.course.getInstructors().remove(this);
        }
        this.course = course;
        if (course != null) {
            course.getInstructors().add(this);
        }
    }
}