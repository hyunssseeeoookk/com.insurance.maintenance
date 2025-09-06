package com.insurance.maintenance.service;

import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.dto.response.CustomerDetailResponseDto;
import com.insurance.maintenance.dto.response.CustomerResponseDto;
import com.insurance.maintenance.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional // 테스트 환경에서 DB롤백을 위한 어노테이션
public class CustomerServiceTest{

    @Autowired private CustomerService customerService;
    @Autowired private CustomerRepository customerRepository;

    @DisplayName("특정 고객의 상세 정보를 조회한다 (계약 정보는 아직 없음).")
    @Test
    void findCustomerDetails_withoutContracts() {
        // given - 오직 고객 데이터만 생성
        Customer savedCustomer = customerRepository.save(
                Customer.builder().name("홍길동").phoneNumber("010-1111-2222").build()
        );

        // when (테스트할 기능 호출)
        CustomerDetailResponseDto customerDetails = customerService.findCustomerDetails(savedCustomer.getId());

        // then (결과 검증)
        // 1. 고객 정보가 올바른가?
        assertThat(customerDetails.getCustomerId()).isEqualTo(savedCustomer.getId());
        assertThat(customerDetails.getName()).isEqualTo("홍길동");

        // 2. 계약 목록 정보는 '비어' 있는 것이 정상인가? - YES
        assertThat(customerDetails.getContracts()).isNotNull(); // Null은 아니어야 함
        assertThat(customerDetails.getContracts()).isEmpty();   // 비어있어야 함
    }

    @DisplayName("고객이 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void findCustomerDetails_throwException_whenCustomerNotFound() {
        // given
        Long notExistingCustomerId = -1L;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.findCustomerDetails(notExistingCustomerId);
        });
    }

    // findAllCustomers 테스트는 [F-01]에서 이미 완성했으므로 생략 가능

}
