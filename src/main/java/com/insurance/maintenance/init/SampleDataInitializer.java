package com.insurance.maintenance.init;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.ContractStatus;
import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.domain.InsuranceProduct;
import com.insurance.maintenance.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local") // "local" 프로필에서만 활성화
@Component
@RequiredArgsConstructor
public class SampleDataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;

    private final EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // [F-01] 기능을 위한 고객 데이터
        //customerRepository.save(Customer.builder().name("홍길동").phoneNumber("010-1111-2222").build());
        //customerRepository.save(Customer.builder().name("둘리").phoneNumber("010-2222-3333").build());

        // [F-02] 기능을 위한 고객/상품/계약 데이터
        Customer customer1 = Customer.builder().name("홍길동").phoneNumber("010-1234-5678").build();
        em.persist(customer1);

        InsuranceProduct productA = new InsuranceProduct("튼튼 건강보험");
        em.persist(productA);

        Contract contract1 = Contract.createContract(customer1, productA, "A-0001", ContractStatus.NORMAL);
        em.persist(contract1);
    }
}
