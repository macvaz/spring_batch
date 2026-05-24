package com.bde.adminprocessing.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "banking_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_code", nullable = false, unique = true, length = 64)
    private String externalCode;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(name = "swift_code", length = 11)
    private String swiftCode;

    @Column(length = 64)
    private String country;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
