package com.insurance.maintenance.dto.response;

import com.insurance.maintenance.domain.Customer;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CustomerDetailResponseDto {
    private Long customerId;
    private String name;
    private String phoneNumber;
    private List<SimpleContractDto> contracts;

    public CustomerDetailResponseDto(Customer customer){
        this.customerId = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
        this.contracts = customer.getContracts().stream()
                .map(SimpleContractDto::new)
                .collect(Collectors.toList());
    }
}
