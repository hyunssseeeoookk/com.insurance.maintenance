package com.insurance.maintenance.service;

import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.dto.response.CustomerDetailDto;
import com.insurance.maintenance.dto.response.CustomerDto;
import com.insurance.maintenance.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // [F-01] 고객 목록 조회
    public List<CustomerDto> findAllCustomers(){
        return customerRepository.findAll().stream()
                .map(CustomerDto::new)  // customer -> new CustomerResponseDto(customer)
                .collect(Collectors.toList());
    }

    // [F-02] 고객 상세 조회
    public CustomerDetailDto findCustomerDetails(Long customerId){
        Customer customer = customerRepository.findByIdWithContracts(customerId)
                .orElseThrow(()->new IllegalArgumentException("해당고객을 찾을 수 없습니다. id ="+customerId));
        return new CustomerDetailDto(customer);
    }
}
