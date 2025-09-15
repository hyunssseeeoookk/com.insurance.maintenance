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

import java.time.LocalDate;

@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class SampleDataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final ContractRepository contractRepository;
    private final InsuranceProductRepository productRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("===== Initializing Sample Data =====");

        // --- 필요한 데이터 초기화 메소드를 여기에 호출 ---
        initForF01_CustomerList();
        initForF03_ContractList(); // F-02 상세 조회에서도 이 데이터를 사용
        initForB01_PaymentBatch(); // B-01 배치 테스트용 데이터 추가

        log.info("===== Sample Data Initialization Finished =====");
    }

    private void initForF01_CustomerList() {
        log.info(">>> Initializing data for [F-01] Customer List");
        // 이 Runner가 실행될 때마다 기존 데이터를 삭제하여 중복을 방지 (선택적)
        contractRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();

        customerRepository.save(Customer.builder().name("고객-F01-홍길동").phoneNumber("010-1111-0001").build());
        customerRepository.save(Customer.builder().name("고객-F01-김유신").phoneNumber("010-2222-0001").build());
    }

    private void initForF03_ContractList() {
        log.info(">>> Initializing data for [F-03] Contract List (used by F-02 as well)");
        Customer customer = customerRepository.save(Customer.builder().name("고객-F03-강감찬").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("계약목록용 보험"));

        // [F-03] 오류 수정: createContract 호출 시 납입일자(임의의 값) 추가
        for (int i = 1; i <= 15; i++) {
            contractRepository.save(
                    Contract.createContract(customer, product, "F03-" + String.format("%04d", i), ContractStatus.NORMAL, 15) // 납입일 15일로 고정
            );
        }
    }

    private void initForB01_PaymentBatch() {
        log.info(">>> Initializing data for [B-01] Payment Batch Test");

        int yesterday = LocalDate.now().minusDays(1).getDayOfMonth();
        int today = LocalDate.now().getDayOfMonth();
        int tomorrow = LocalDate.now().plusDays(1).getDayOfMonth();

        Customer customer = customerRepository.save(Customer.builder().name("고객-B01-이순신").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("배치테스트용 보험"));

        // 시나리오 1: 성공 대상 (정상, 어제)
        contractRepository.save(Contract.createContract(customer, product, "B01-SUCCESS-1", ContractStatus.NORMAL, yesterday));
        contractRepository.save(Contract.createContract(customer, product, "B01-SUCCESS-2", ContractStatus.NORMAL, yesterday));

        // 시나리오 2: 실패 대상 (실효, 어제)
        contractRepository.save(Contract.createContract(customer, product, "B01-FAIL-LAPSE", ContractStatus.LAPSE, yesterday));

        // 시나리오 3: 실패 대상 (정상, 오늘)
        contractRepository.save(Contract.createContract(customer, product, "B01-FAIL-TODAY", ContractStatus.NORMAL, today));

        // 시나리오 4: 실패 대상 (정상, 내일)
        contractRepository.save(Contract.createContract(customer, product, "B01-FAIL-TOMORROW", ContractStatus.NORMAL, tomorrow));
    }
}