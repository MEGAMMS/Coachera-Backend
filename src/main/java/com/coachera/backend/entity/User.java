package com.coachera.backend.entity;

import java.util.HashSet;
import java.util.Set;

import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.OrganizationDTO;
import com.coachera.backend.dto.RoleDTO;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.enums.RoleType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @OneToOne
    @JoinColumn
    private Image profileImage;

    private Boolean isVerified;

    // Bidirectional relationship with Organization
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Organization organization;

    // Bidirectional relationship with Student
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Student student;

    // Bidirectional relationship with Instructor
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Instructor instructor;

    // Bidirectional relationship with Device Token
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<DeviceToken> deviceTokens = new HashSet<>();

    // Helper methods for managing relationships
    public void setOrganization(Organization organization) {
        if (organization == null) {
            if (this.organization != null) {
                this.organization.setUser(null);
            }
        } else {
            organization.setUser(this);
        }
        this.organization = organization;
    }

    // User.java
    public void setStudent(Student student) {
        this.student = student;
        if (student != null && student.getUser() != this) {
            student.setUser(this);
        }
    }


    public void setInstructor(Instructor instructor) {
        if (instructor == null) {
            if (this.instructor != null) {
                this.instructor.setUser(null);
            }
        } else {
            instructor.setUser(this);
        }
        this.instructor = instructor;
    }

    // Convenience method to check user type
    public boolean isOrganization() {
        return this.organization != null;
    }

    public boolean isStudent() {
        return this.student != null;
    }

    public boolean isInstructor() {
        return this.instructor != null;
    }


    public RoleDTO getRoleDetails(){
        RoleDTO roleDetails = null;
        if (this.isStudent()) {
            roleDetails = new StudentDTO(this.getStudent());
        } else if (this.isInstructor()) {
            roleDetails = new InstructorDTO(this.getInstructor());
        } else if (this.isOrganization()) {
            roleDetails = new OrganizationDTO(this.getOrganization());
        }
        
        return roleDetails;
    }
}