package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {
}
