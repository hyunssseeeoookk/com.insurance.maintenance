package com.insurance.maintenance.controller.api;

import com.insurance.maintenance.dto.response.CustomerDetailResponseDto;
import com.insurance.maintenance.dto.response.CustomerResponseDto;
import com.insurance.maintenance.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers") // url 경로를 /api/customers로 명확히 구분
@RequiredArgsConstructor
public class CustomerApiController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getCustomer(){
        List<CustomerResponseDto> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailResponseDto> getCustomerDetails(@PathVariable Long id){
        CustomerDetailResponseDto customerDetails = customerService.findCustomerDetails(id);
        return ResponseEntity.ok(customerDetails);
    }
}
