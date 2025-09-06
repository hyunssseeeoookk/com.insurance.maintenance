package com.insurance.maintenance.dto.response;

import com.insurance.maintenance.domain.Customer;
import lombok.Getter;

@Getter
public class CustomerResponseDto {

    private Long id;
    private String name;
    private String phoneNumber;

    public CustomerResponseDto(Customer customer){
        this.id = customer.getId();
        this.name = customer.getName();
        this.phoneNumber = customer.getPhoneNumber();
    }
}
