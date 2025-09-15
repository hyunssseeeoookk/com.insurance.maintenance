package com.insurance.maintenance.batch;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.repository.ContractRepository;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProcessLapseJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ContractRepository contractRepository;
    private final int CHUNK_SIZE = 10;

    @Bean
    public Job processLapseJob() {
        return new JobBuilder("processLapseJob", jobRepository)
                .start(lapseStep())
                .build();
    }

    @Bean
    public Step lapseStep() {
        return new StepBuilder("lapseStep", jobRepository)
                .<Contract, Contract>chunk(CHUNK_SIZE, transactionManager)
                .reader(lapseTargetReader())
                .processor(lapseProcessor())
                .writer(contractStateUpdateWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Contract> lapseTargetReader() {
        // 기준 날짜를 '3개월 전 오늘'로 명확하게 계산
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        return new RepositoryItemReaderBuilder<Contract>()
                .name("lapseTargetReader")
                .repository(contractRepository)
                .methodName("findLapseTargets")
                .pageSize(CHUNK_SIZE)
                .arguments(List.of(threeMonthsAgo)) // 계산된 LocalDate를 파라미터로 전달
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Contract, Contract> lapseProcessor() {
        return contract -> {
            String reason = "최종납입월 3개월 경과";
            log.info("Processing Lapse for Contract ID: {}, Last Payment: {}", contract.getId(), contract.getLastPaymentDate());
            contract.toLapse(reason);
            return contract;
        };
    }

    @Bean
    public JpaItemWriter<Contract> contractStateUpdateWriter() {
        JpaItemWriter<Contract> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}