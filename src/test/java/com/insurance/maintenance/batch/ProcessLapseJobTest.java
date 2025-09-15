package com.insurance.maintenance.batch;

import com.insurance.maintenance.domain.*;
import com.insurance.maintenance.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

@SpringBatchTest
@SpringBootTest // 어떠한 추가 설정도 없이, 가장 기본적이고 강력한 형태로 사용합니다.
class ProcessLapseJobTest {

    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired @Qualifier("processLapseJob") private Job jobToTest;

    @Autowired private ContractRepository contractRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private InsuranceProductRepository productRepository;
    @Autowired private LapseHistoryRepository lapseHistoryRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    @AfterEach
    void tearDown() {
        lapseHistoryRepository.deleteAllInBatch();
        contractRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("최종납입일이 3개월 이전인 정상 계약만 실효 처리한다.")
    void processLapseJob_shouldLapse_whenLastPaymentIs3MonthsAgo() throws Exception {
        // given
        Customer customer = customerRepository.save(Customer.builder().name("실효대상고객").build());
        InsuranceProduct product = productRepository.save(new InsuranceProduct("실효테스트상품"));
        Contract targetContract = Contract.createContract(customer, product, "LAPSE-TARGET", ContractStatus.NORMAL, 15);
        targetContract.updateLastPaymentDateForTest(LocalDate.now().minusMonths(3));
        contractRepository.save(targetContract);
        Contract nonTargetContract = Contract.createContract(customer, product, "NON-TARGET", ContractStatus.NORMAL, 15);
        nonTargetContract.updateLastPaymentDateForTest(LocalDate.now().minusMonths(2));
        contractRepository.save(nonTargetContract);

        // when
        jobLauncherTestUtils.setJob(jobToTest);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            Contract updatedTarget = contractRepository.findByIdWithLapseHistories(targetContract.getId()).get();
            assertThat(updatedTarget.getStatus()).isEqualTo(ContractStatus.LAPSE);
            assertThat(updatedTarget.getLapseHistories()).hasSize(1);

            Contract updatedNonTarget = contractRepository.findById(nonTargetContract.getId()).get();
            assertThat(updatedNonTarget.getStatus()).isEqualTo(ContractStatus.NORMAL);
            return null;
        });
    }
}