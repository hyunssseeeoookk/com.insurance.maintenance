package com.insurance.maintenance.batch;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.ContractStatus;
import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.domain.InsuranceProduct;
import com.insurance.maintenance.repository.ContractRepository;
import com.insurance.maintenance.repository.CustomerRepository;
import com.insurance.maintenance.repository.InsuranceProductRepository;
import com.insurance.maintenance.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

@SpringBatchTest
@SpringBootTest
class ProcessNormalPaymentJobTest {

    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired @Qualifier("processNormalPaymentJob")
    private Job processNormalPaymentJob;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private InsuranceProductRepository productRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(processNormalPaymentJob);

        Customer customer = customerRepository.save(Customer.builder().name("테스트고객").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("테스트상품"));

        // given: 정상 계약 2건, 실효 계약 1건
        // -----[여기가 핵심 수정 부분]-----
        int targetDate = LocalDate.now().minusDays(1).getDayOfMonth(); // 어제 날짜

        // createContract 호출 시, 마지막 인자로 paymentDueDate(targetDate)를 추가합니다.
        contractRepository.save(Contract.createContract(customer, product, "N-001", ContractStatus.NORMAL, targetDate));
        contractRepository.save(Contract.createContract(customer, product, "N-002", ContractStatus.NORMAL, targetDate));
        contractRepository.save(Contract.createContract(customer, product, "L-001", ContractStatus.LAPSE, targetDate));
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAllInBatch();
        contractRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
    }

    @DisplayName("정상 상태의 계약에 대해서만 납입 이력을 생성한다.")
    @Test
    void processNormalPaymentJob_createsPayments_onlyForNormalContracts() throws Exception {
        // given
        int yesterdayDay = LocalDate.now().minusDays(1).getDayOfMonth();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("run.id", "paymentJobTest-" + System.currentTimeMillis())
                .addLong("yesterdayDay", (long) yesterdayDay)
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        // setUp에서 만든 '정상' 계약 2건에 대해서만 Payment가 생성되어야 합니다.
        assertThat(paymentRepository.count()).isEqualTo(2);
    }
}