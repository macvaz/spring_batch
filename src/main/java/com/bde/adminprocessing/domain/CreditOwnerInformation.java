package com.bde.adminprocessing.domain;

import com.bde.adminprocessing.domain.enums.CreditOwnerRole;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "credit_owners_information",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_credit_owner_national_id_input",
                columnNames = {"national_id", "input_document_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditOwnerInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "input_document_id", nullable = false)
    private InputDocument inputDocument;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(name = "national_id", nullable = false, length = 64)
    private String nationalId;

    @Column(length = 256)
    private String email;

    @Column(length = 64)
    private String phone;

    @Column(length = 512)
    private String address;

    @Column(name = "ownership_percentage", precision = 5, scale = 2)
    private BigDecimal ownershipPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CreditOwnerRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CreditOperationBlocked> blockedOperations = new ArrayList<>();

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (role == null) {
            role = CreditOwnerRole.PRIMARY;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
