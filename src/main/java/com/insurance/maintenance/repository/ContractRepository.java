package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract,Long> {
}
