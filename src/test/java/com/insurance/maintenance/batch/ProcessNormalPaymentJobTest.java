package com.insurance.maintenance.batch;

import com.insurance.maintenance.domain.*;
import com.insurance.maintenance.repository.*;
import org.junit.jupiter.api.AfterEach;
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
@SpringBootTest // 어떠한 추가 설정도 없이, 가장 기본적이고 강력한 형태로 사용합니다.
class ProcessNormalPaymentJobTest {

    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired @Qualifier("processNormalPaymentJob") private Job jobToTest;

    @Autowired private CustomerRepository customerRepository;
    @Autowired private InsuranceProductRepository productRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private PaymentRepository paymentRepository;

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAllInBatch();
        contractRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("정상 상태이고 어제가 납입일인 계약 2건에 대해서만 납입 이력을 생성한다.")
    void processNormalPaymentJob_createsPayments_onlyForTargetContracts() throws Exception {
        // given
        int yesterdayDay = LocalDate.now().minusDays(1).getDayOfMonth();
        Customer customer = customerRepository.save(Customer.builder().name("테스트고객").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("테스트상품"));

        contractRepository.save(Contract.createContract(customer, product, "N-YESTERDAY-1", ContractStatus.NORMAL, yesterdayDay));
        contractRepository.save(Contract.createContract(customer, product, "N-YESTERDAY-2", ContractStatus.NORMAL, yesterdayDay));
        contractRepository.save(Contract.createContract(customer, product, "L-YESTERDAY-1", ContractStatus.LAPSE, yesterdayDay));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("run.id", "normalJobTest-" + System.currentTimeMillis())
                .addLong("yesterdayDay", (long) yesterdayDay)
                .toJobParameters();

        // when
        jobLauncherTestUtils.setJob(jobToTest);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        assertThat(paymentRepository.count()).isEqualTo(2);
    }
}