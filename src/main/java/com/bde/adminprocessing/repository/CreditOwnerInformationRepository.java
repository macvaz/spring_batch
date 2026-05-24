package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.CreditOwnerInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CreditOwnerInformationRepository extends JpaRepository<CreditOwnerInformation, Long> {

    List<CreditOwnerInformation> findByInputDocumentId(Long inputDocumentId);

    Optional<CreditOwnerInformation> findByNationalIdAndInputDocumentId(String nationalId, Long inputDocumentId);

    Optional<CreditOwnerInformation> findByNationalId(String nationalId);

    @Query("SELECT c FROM CreditOwnerInformation c JOIN FETCH c.inputDocument WHERE c.id = :id")
    Optional<CreditOwnerInformation> findByIdWithInput(Long id);
}
