package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    /**
     * 특정 고객 정보를 조회할 때, 연관된 계약(contracts) 정보까지 한 번의 쿼리로 함께 가져온다. (N+1 문제 해결)
     */
    @Query("select c from Customer c left join fetch c.contracts where c.id = :id")
    Optional<Customer> findByIdWithContracts(@Param("id") Long id);
}
