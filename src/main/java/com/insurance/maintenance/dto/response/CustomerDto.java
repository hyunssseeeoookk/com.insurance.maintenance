package com.insurance.maintenance.dto.response;

import com.insurance.maintenance.domain.Customer;
import lombok.Getter;

/**
 * 고객 목록 조회 시 사용될 기본 DTO
 */
@Getter
public class CustomerDto {
    private Long id;
    private String name;
    private String phoneNumber;

    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
    }
}
