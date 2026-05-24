package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

    Optional<Procedure> findByExternalCode(String externalCode);
}
