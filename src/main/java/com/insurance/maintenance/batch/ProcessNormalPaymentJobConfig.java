package com.insurance.maintenance.batch;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.Payment;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProcessNormalPaymentJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 10;

    @Bean
    public Job processNormalPaymentJob(){
        return new JobBuilder("processNormalPaymentJob",jobRepository)
                .start(processNormalPaymentStep())
                .build();
    }

    @Bean
    public Step processNormalPaymentStep(){
        return new StepBuilder("processNormalPaymentStep",jobRepository)
                .<Contract, Payment>chunk(CHUNK_SIZE,transactionManager)
                .reader(contractReader(null)) // jobParameters를 받기 위해 null로 초기화
                .processor(paymentProcessor())
                .writer(paymentWriter())
                .build();

    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Contract> contractReader(
            // 이 시점에는 jobParameters가 주입될 수 있음
            @Value("#{jobParameters['yesterdayDay']}") Long yesterdayDay) {

        // Map.of는 null을 허용하지 않으므로, Objects.requireNonNull로 명시적인 예외를 발생시키는 것이 좋음
        Objects.requireNonNull(yesterdayDay, "yesterdayDay 파라미터가 없습니다.");

        return new JpaPagingItemReaderBuilder<Contract>()
                .name("contractReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select c from Contract c where c.status = 'NORMAL' and c.paymentDueDate = :dueDate")
                .parameterValues(Map.of("dueDate", yesterdayDay.intValue()))
                .build();
    }

    @Bean
    public ItemProcessor<Contract, Payment> paymentProcessor() {
        return contract -> {
            BigDecimal premium = new BigDecimal("30000");
            int nextSequence = contract.getPayments().size() + 1;
            log.info("Processing contractId: {}, Creating payment sequence: {}", contract.getId(), nextSequence);
            // 어제 날짜로 납입 이력 생성
            return new Payment(LocalDate.now().minusDays(1), premium, nextSequence, contract);
        };
    }

    @Bean
    public JpaItemWriter<Payment> paymentWriter(){
        JpaItemWriter<Payment> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
