package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.BankingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankingEntityRepository extends JpaRepository<BankingEntity, Long> {

    Optional<BankingEntity> findByExternalCode(String externalCode);
}
