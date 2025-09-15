package com.insurance.maintenance.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final JobLauncher jobLauncher; // Spring Batch가 제공하는 Job 실행기

    // 실행할 Job을 이름으로 정확히 주입받습니다.
    @Qualifier("processNormalPaymentJob")
    private final Job processNormalPaymentJob;

    /**
     * 매월 25일 오전 9시에 정상납 처리 배치 실행
     * cron = "초 분 시 일 월 요일"
     * (주의: 운영 환경에서는 장애 상황 등을 고려하여 스케줄링 중복 실행 방지(ShedLock 등)가 필요할 수 있습니다.)
     */
    // @Scheduled(cron = "0 0 9 25 * *")
    @Scheduled(cron = "0 * * * * *") // 개발 및 테스트를 위해 '매 1분마다' 실행하도록 임시 변경
    public void runPaymentJob() {
        log.info(">>>>>> [Scheduler] Starting monthly payment batch job trigger...");

        // JobParameter 생성: 어제 날짜의 '일(day)' 값을 동적으로 전달
        int yesterdayDay = LocalDate.now().minusDays(1).getDayOfMonth();

        // 동일한 파라미터로 Job을 두 번 실행할 수 없으므로, 매번 다른 값을 주기 위해 현재 시간을 추가
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", "paymentJob-" + System.currentTimeMillis())
                .addLong("yesterdayDay", (long) yesterdayDay)
                .toJobParameters();

        try {
            // JobLauncher를 사용하여 Job을 실행
            jobLauncher.run(processNormalPaymentJob, params);
        } catch (Exception e) {
            log.error("!!!!!!!!!! [Scheduler] Failed to run payment batch job.", e);
        }
    }
}