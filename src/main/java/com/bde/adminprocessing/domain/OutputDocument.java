package com.bde.adminprocessing.domain;

import com.bde.adminprocessing.domain.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "output_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "input_document_id", nullable = false)
    private InputDocument inputDocument;

    @Column(name = "signature_reference", length = 128)
    private String signatureReference;

    @Column(name = "notification_reference", length = 128)
    private String notificationReference;

    @Column(name = "delivery_channel", length = 64)
    private String deliveryChannel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DocumentStatus status;

    @Column(name = "produced_at", nullable = false)
    private Instant producedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @PrePersist
    void onCreate() {
        if (producedAt == null) {
            producedAt = Instant.now();
        }
        if (status == null) {
            status = DocumentStatus.READY;
        }
    }
}
