package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("select c from Customer c left join fetch c.contracts where c.id = :id")
    Optional<Customer> findCustomerByIdWithContracts(@Param("id") Long id);
}
