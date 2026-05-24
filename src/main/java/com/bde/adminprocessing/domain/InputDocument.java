package com.bde.adminprocessing.domain;

import com.bde.adminprocessing.domain.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "input_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @Column(name = "citizen_id", nullable = false, length = 64)
    private String citizenId;

    @Column(name = "submission_channel", length = 64)
    private String submissionChannel;

    @Column(name = "source_file", length = 512)
    private String sourceFile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private DocumentStatus status;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message", length = 1024)
    private String errorMessage;

    @OneToMany(mappedBy = "inputDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutputDocument> outputDocuments = new ArrayList<>();

    @OneToMany(mappedBy = "inputDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CreditOwnerInformation> creditOwners = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (receivedAt == null) {
            receivedAt = Instant.now();
        }
        if (status == null) {
            status = DocumentStatus.RECEIVED;
        }
    }
}
