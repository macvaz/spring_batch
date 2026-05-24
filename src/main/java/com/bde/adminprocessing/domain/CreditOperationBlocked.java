package com.bde.adminprocessing.domain;

import com.bde.adminprocessing.domain.enums.CreditBlockStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "credit_operation_blocked",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_credit_operation_id",
                columnNames = "operation_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditOperationBlocked {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_id", nullable = false, length = 64)
    private String operationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private CreditOwnerInformation owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "banking_entity_id", nullable = false)
    private BankingEntity bankingEntity;

    @Column(name = "blocked_amount", precision = 19, scale = 2)
    private BigDecimal blockedAmount;

    @Column(length = 3)
    private String currency;

    @Column(name = "block_reason", length = 512)
    private String blockReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "block_status", nullable = false, length = 32)
    private CreditBlockStatus blockStatus;

    @Column(name = "blocked_at", nullable = false)
    private Instant blockedAt;

    @Column(name = "released_at")
    private Instant releasedAt;

    @Column(name = "external_reference", length = 128)
    private String externalReference;

    @Column(name = "block_month", length = 7)
    private String blockMonth;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (blockedAt == null) {
            blockedAt = now;
        }
        if (blockStatus == null) {
            blockStatus = CreditBlockStatus.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
