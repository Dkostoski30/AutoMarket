package com.automarket.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "condition_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConditionType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
