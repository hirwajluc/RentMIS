package com.rentmis.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wings", indexes = {
        @Index(name = "idx_wings_property", columnList = "property_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // e.g. "Wing A", "Block B", "East Wing"

    @Column(length = 300)
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @OneToMany(mappedBy = "wing", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Unit> units = new ArrayList<>();
}
