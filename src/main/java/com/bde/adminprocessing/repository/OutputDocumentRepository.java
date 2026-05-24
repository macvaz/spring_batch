package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.OutputDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OutputDocumentRepository extends JpaRepository<OutputDocument, Long> {

    @Query("SELECT o FROM OutputDocument o JOIN FETCH o.document WHERE o.inputDocument.id = :inputDocumentId")
    List<OutputDocument> findByInputDocumentId(Long inputDocumentId);

    @Query("SELECT o FROM OutputDocument o JOIN FETCH o.document JOIN FETCH o.inputDocument i JOIN FETCH i.document WHERE o.id = :id")
    Optional<OutputDocument> findByIdWithDetails(Long id);

    long countBySignatureReferenceIsNotNull();
}
