package com.insurance.maintenance.init;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SampleDataInitializer implements ApplicationRunner {
    // CustomerRepository에 대한 의존성만 남깁니다.
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // [F-02] 단계에서는 계약 데이터를 미리 생성할 책임이 없습니다.
        // 오직 고객 데이터만 생성하여 [F-01] 기능이 정상 동작하도록 보장합니다.

        // -- 샘플 고객 데이터 생성 --
        Customer customer1 = Customer.builder()
                .name("홍길동")
                .phoneNumber("010-1111-2222")
                .build();

        Customer customer2 = Customer.builder()
                .name("김유신")
                .phoneNumber("010-2222-3333")
                .build();

        // -- Repository를 통해 DB에 저장 --
        customerRepository.save(customer1);
        customerRepository.save(customer2);
    }
}
