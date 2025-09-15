package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query(value = "select c from Contract c " +
            "join fetch c.customer cu " +
            "join fetch c.product p",
            countQuery = "select count(c) from Contract c")
    Page<Contract> findAllWithDetails(Pageable pageable);

    @Query("select c from Contract c " +
            "where c.status = 'NORMAL' " +
            "and c.lastPaymentDate <= :threeMonthsAgo")
    Page<Contract> findLapseTargets(@Param("threeMonthsAgo") LocalDate threeMonthsAgo, Pageable pageable);

    @Query("select c from Contract c left join fetch c.lapseHistories where c.id = :id")
    Optional<Contract> findByIdWithLapseHistories(@Param("id") Long id);
}