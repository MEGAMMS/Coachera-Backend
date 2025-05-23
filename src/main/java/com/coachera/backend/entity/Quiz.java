package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quizzes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "material_id", nullable = false, unique = true)
    private Material material;
}