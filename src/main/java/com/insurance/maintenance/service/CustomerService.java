package com.insurance.maintenance.service;

import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.dto.response.CustomerDetailResponseDto;
import com.insurance.maintenance.dto.response.CustomerResponseDto;
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

    public List<CustomerResponseDto> findAllCustomers(){
        return customerRepository.findAll().stream()
                .map(CustomerResponseDto::new)  // customer -> new CustomerResponseDto(customer)
                .collect(Collectors.toList());
    }

    public CustomerDetailResponseDto findCustomerDetails(Long customerId){
        Customer customer = customerRepository.findCustomerByIdWithContracts(customerId)
                .orElseThrow(()->new IllegalArgumentException("해당고객을 찾을 수 없습니다. id ="+customerId));
        return new CustomerDetailResponseDto(customer);
    }
}
