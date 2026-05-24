package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.InputDocument;
import com.bde.adminprocessing.domain.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InputDocumentRepository extends JpaRepository<InputDocument, Long> {

    Optional<InputDocument> findByDocumentDocumentCode(String documentCode);

    List<InputDocument> findByStatus(DocumentStatus status);

    @Query("SELECT i FROM InputDocument i JOIN FETCH i.document JOIN FETCH i.procedure WHERE i.status = :status")
    List<InputDocument> findByStatusWithDetails(DocumentStatus status);

    @Query("SELECT i FROM InputDocument i JOIN FETCH i.document JOIN FETCH i.procedure LEFT JOIN FETCH i.outputDocuments o LEFT JOIN FETCH o.document WHERE i.id = :id")
    Optional<InputDocument> findByIdWithOutputs(Long id);

    List<InputDocument> findByProcedureIdAndStatusNot(Long procedureId, DocumentStatus status);
}
