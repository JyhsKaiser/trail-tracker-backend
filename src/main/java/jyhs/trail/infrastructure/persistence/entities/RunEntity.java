package jyhs.trail.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "runs")
@Getter
@Setter // Usamos Lombok para mantenerlo limpio
public class RunEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double distanceKm;
    private int elevationGain;
    private LocalDate date;

    // Relación Many-to-One: Muchas carreras pertenecen a un Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}