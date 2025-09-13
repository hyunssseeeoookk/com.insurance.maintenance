package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.ContractStatus;
import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.domain.InsuranceProduct;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EntityManager em;

    private Customer customer;

    @BeforeEach
    void setUp(){
        // given: 테스트를 위한 데이터 준비
        customer = Customer.builder().name("테스트고객").phoneNumber("010-1234-5678").build();
        InsuranceProduct product = new InsuranceProduct("테스트상품");
        em.persist(customer);
        em.persist(product);

        Contract contract1 = Contract.createContract(customer, product, "A-001", ContractStatus.NORMAL);
        Contract contract2 = Contract.createContract(customer, product, "A-002", ContractStatus.LAPSE);
        em.persist(contract1);
        em.persist(contract2);

        em.flush(); // db에 전달
        em.clear(); // 영속성 컨텍스트 초기화
    }

    @DisplayName("FindByIdWithContracts는 고객과 계약 정보를 페치조인으로 함께 조회한다")
    @Test
    public void findByIdWithContracts_shouldFetchContract(){
        //when : 페치조인 쿼리 싫행
        Customer findCustomer = customerRepository.findByIdWithContracts(customer.getId()).get();

        //then : 결과 검증
        Assertions.assertThat(findCustomer.getName()).isEqualTo("테스트고객");
        Assertions.assertThat(findCustomer.getContracts()).hasSize(2); // 연관된 계약이 2건 모두 포함되어 있는지
        Assertions.assertThat(findCustomer.getContracts().get(0).getAccountNo()).isEqualTo("A-001");
    }
}
