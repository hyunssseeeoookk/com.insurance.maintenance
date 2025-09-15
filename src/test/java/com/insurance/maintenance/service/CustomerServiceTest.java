package com.insurance.maintenance.service;

// ... imports ...
import com.insurance.maintenance.domain.*;
import com.insurance.maintenance.dto.response.CustomerDetailDto;
import com.insurance.maintenance.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private InsuranceProductRepository productRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(Customer.builder().name("홍길동").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("테스트상품"));
        contractRepository.save(Contract.createContract(customer, product, "A-001", ContractStatus.NORMAL,25));
        contractRepository.save(Contract.createContract(customer, product, "A-002", ContractStatus.LAPSE,25));
    }

    @DisplayName("특정 고객의 상세 정보와 보유 계약 목록을 함께 조회한다.")
    @Test
    void findCustomerDetails() {
        // when
        CustomerDetailDto customerDetails = customerService.findCustomerDetails(customer.getId());

        // then
        assertThat(customerDetails.getName()).isEqualTo("홍길동");
        assertThat(customerDetails.getContracts()).hasSize(2);
        assertThat(customerDetails.getContracts()).extracting("accountNo")
                .containsExactlyInAnyOrder("A-001", "A-002");
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
}