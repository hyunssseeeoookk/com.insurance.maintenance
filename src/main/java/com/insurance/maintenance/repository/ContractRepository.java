package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContractRepository extends JpaRepository<Contract,Long> {
    @Query(value = "select c from Contract c " +
            "join fetch c.customer cu " +
            "join fetch c.product p",
            countQuery = "select count(c) from Contract c")
    Page<Contract> findAllWithDetails(Pageable pageable);
}
