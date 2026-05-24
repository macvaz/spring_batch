package com.bde.adminprocessing.repository;

import com.bde.adminprocessing.domain.CreditOperationBlocked;
import com.bde.adminprocessing.domain.enums.CreditBlockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CreditOperationBlockedRepository extends JpaRepository<CreditOperationBlocked, Long> {

    Optional<CreditOperationBlocked> findByOperationId(String operationId);

    @Query("""
            SELECT b FROM CreditOperationBlocked b
            JOIN FETCH b.owner
            JOIN FETCH b.bankingEntity
            WHERE b.operationId = :operationId
            """)
    Optional<CreditOperationBlocked> findByOperationIdWithDetails(String operationId);

    List<CreditOperationBlocked> findByOwnerId(Long ownerId);

    List<CreditOperationBlocked> findByBankingEntityId(Long bankingEntityId);

    List<CreditOperationBlocked> findByBlockStatus(CreditBlockStatus blockStatus);

    @Query("""
            SELECT b FROM CreditOperationBlocked b
            JOIN FETCH b.owner
            JOIN FETCH b.bankingEntity
            WHERE b.blockStatus = :status
            """)
    List<CreditOperationBlocked> findByBlockStatusWithDetails(CreditBlockStatus status);

    @Query("""
            SELECT b FROM CreditOperationBlocked b
            JOIN FETCH b.owner
            JOIN FETCH b.bankingEntity
            """)
    List<CreditOperationBlocked> findAllWithDetails();

    @Query("""
            SELECT b FROM CreditOperationBlocked b
            JOIN FETCH b.owner
            JOIN FETCH b.bankingEntity
            WHERE b.id = :id
            """)
    Optional<CreditOperationBlocked> findByIdWithDetails(Long id);

    @Query("""
            SELECT b FROM CreditOperationBlocked b
            JOIN FETCH b.owner
            JOIN FETCH b.bankingEntity
            WHERE b.owner.id = :ownerId
            """)
    List<CreditOperationBlocked> findByOwnerIdWithDetails(Long ownerId);
}
