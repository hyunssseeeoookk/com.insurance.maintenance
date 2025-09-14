package com.insurance.maintenance.batch;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.Payment;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;

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
                .reader(contractReader())
                .processor(paymentProcessor())
                .writer(paymentWriter())
                .build();

    }

    @Bean
    public JpaPagingItemReader<Contract> contractReader(){
        return new JpaPagingItemReaderBuilder<Contract>()
                .name("contractReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select c from Contract c where c.status = 'NORMAL' order by c.id")
                .build();
    }

    @Bean
    public ItemProcessor<Contract,Payment> paymentProcessor(){
        return contract -> {
            // 이 부분은 예시, 실제로는 상품정보에서 보험료를 가져오는 로직 추가 필요]
            BigDecimal premium = new BigDecimal("30000");
            int nextSequence = contract.getPayments().size()+1;

            log.info("Processing contractId : {}, Creating payment sequence : {}", contract.getId(), nextSequence);

            return new Payment(LocalDate.now(), premium, nextSequence, contract);
        };
    }

    @Bean
    public JpaItemWriter<Payment> paymentWriter(){
        JpaItemWriter<Payment> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
