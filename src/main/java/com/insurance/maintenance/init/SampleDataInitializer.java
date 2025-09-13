package com.insurance.maintenance.init;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.ContractStatus;
import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.domain.InsuranceProduct;
import com.insurance.maintenance.repository.ContractRepository;
import com.insurance.maintenance.repository.CustomerRepository;
import com.insurance.maintenance.repository.InsuranceProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class SampleDataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;
    private final InsuranceProductRepository productRepository;

    /**
     * 실행 시점의 진입점.
     * 필요한 기능의 초기화 메소드를 선택적으로 호출.
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("===== Initializing Sample Data =====");

        // [F-01] 고객 목록 조회 기능만 테스트하고 싶을 때:
        //initForF01_CustomerList();

        // [F-02] 고객 상세 조회 기능만 테스트하고 싶을 때:
        // initForF02_CustomerDetail();

        // [F-03] 계약 목록 조회 기능만 테스트하고 싶을 때:
         initForF03_ContractList();

        log.info("===== Sample Data Initialization Finished =====");
    }

    /**
     * [F-01] 고객 목록 조회 기능을 위한 독립적인 샘플 데이터를 생성.
     */
    private void initForF01_CustomerList() {
        log.info(">>> Initializing data for [F-01] Customer List");
        customerRepository.save(Customer.builder().name("둘리-F01").phoneNumber("010-1111-0001").build());
        customerRepository.save(Customer.builder().name("아구몬-F01").phoneNumber("010-2222-0001").build());
    }

    /**
     * [F-02] 고객 상세 조회 기능을 위한 독립적인 샘플 데이터를 생성.
     */
    private void initForF02_CustomerDetail() {
        log.info(">>> Initializing data for [F-02] Customer Detail");
        // 1. 이 기능 테스트에 필요한 고객을 새로 생성
        Customer customerForF02 = customerRepository.save(
                Customer.builder().name("파이리-F02").phoneNumber("010-3333-0002").build()
        );

        // 2. 이 기능 테스트에 필요한 상품을 새로 생성
        InsuranceProduct productForF02 = productRepository.save(new InsuranceProduct("상세조회용 보험"));

        // 3. 위에서 만든 고객과 상품으로 계약 데이터를 생성
        contractRepository.save(Contract.createContract(customerForF02, productForF02, "F02-0001", ContractStatus.NORMAL));
        contractRepository.save(Contract.createContract(customerForF02, productForF02, "F02-0002", ContractStatus.LAPSE));
    }

    /**
     * [F-03] 계약 목록 조회 기능을 위한 독립적인 샘플 데이터를 생성.
     */
    private void initForF03_ContractList() {
        log.info(">>> Initializing data for [F-03] Contract List");
        // 1. 페이징 테스트에 필요한 고객과 상품을 새로 생성
        Customer customerForF03 = customerRepository.save(
                Customer.builder().name("피카츄-F03").phoneNumber("010-4444-0003").build()
        );
        InsuranceProduct productForF03 = productRepository.save(new InsuranceProduct("계약목록용 보험"));

        // 2. 페이징을 확인할 수 있도록 계약 데이터를 15건 대량 생성
        for (int i = 1; i <= 15; i++) {
            contractRepository.save(
                    Contract.createContract(customerForF03, productForF03, "F03-" + String.format("%04d", i), ContractStatus.NORMAL)
            );
        }
    }
}