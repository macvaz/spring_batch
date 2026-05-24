package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByDocumentCode(String documentCode);
}
