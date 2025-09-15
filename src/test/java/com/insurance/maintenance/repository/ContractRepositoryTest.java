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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ContractRepositoryTest {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setup(){
        Customer customer = Customer.builder().name("테스트고객").build();
        InsuranceProduct product = new InsuranceProduct("테스트상품");
        em.persist(customer);
        em.persist(product);

        // 페이징 테스트를 위해 5개의 계약 데이터 생성
        int dueDate = 15; // 임의의 납입일자 (예: 15일)
        for (int i = 0; i < 5; i++) {
            // [수정] 마지막 파라미터로 dueDate를 추가합니다.
            em.persist(Contract.createContract(customer, product, "A-00" + i, ContractStatus.NORMAL, dueDate));
        }

        em.flush();
        em.clear();
    }

    @DisplayName("페이징과 페치조인이 적용된 계약목록을 조회한다")
    @Test
    public void findAllWithDetail_Paging(){
        // given
        PageRequest pageRequest = PageRequest.of(0,3);

        // when
        Page<Contract> contractPage = contractRepository.findAllWithDetails(pageRequest);

        // then
        assertThat(contractPage.getContent()).hasSize(3); // 조회된 데이터 건수
        assertThat(contractPage.getTotalElements()).isEqualTo(5); // 전체 데이터 건수
        assertThat(contractPage.getTotalPages()).isEqualTo(2); // 전체 페이지 수
        assertThat(contractPage.getContent().get(0).getCustomer().getName()).isEqualTo("테스트고객");
    }
}
