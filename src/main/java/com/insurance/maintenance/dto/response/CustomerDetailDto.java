package com.insurance.maintenance.dto.response;

import com.insurance.maintenance.domain.Customer;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 고객 상세 정보를 담는 DTO. 계약 목록을 포함.
 */
@Getter
public class CustomerDetailDto {
    private Long customerId;
    private String name;
    private String phoneNumber;
    private List<ContractSimpleDto> contracts;

    public CustomerDetailDto(Customer customer) {
        this.customerId = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
        this.contracts = customer.getContracts().stream()
                .map(ContractSimpleDto::new)
                .collect(Collectors.toList());
    }
}