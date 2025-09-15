package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 통합 테스트 환경에서는 @Transactional을 붙여 자동 롤백을 활용
class CustomerRepositoryTest {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private InsuranceProductRepository productRepository;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("findByIdWithContracts는 고객과 계약 정보를 페치 조인으로 함께 조회한다.")
    void findByIdWithContracts_shouldFetchContracts() {
        // given
        Customer customer = customerRepository.save(Customer.builder().name("테스트고객").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("테스트상품"));
        contractRepository.save(Contract.createContract(customer, product, "A-001", ContractStatus.NORMAL, 15));

        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화로 DB에서 직접 가져오도록 함

        // when
        Customer foundCustomer = customerRepository.findByIdWithContracts(customer.getId()).get();

        // then
        assertThat(foundCustomer.getName()).isEqualTo("테스트고객");
        assertThat(foundCustomer.getContracts()).hasSize(1);
    }
}