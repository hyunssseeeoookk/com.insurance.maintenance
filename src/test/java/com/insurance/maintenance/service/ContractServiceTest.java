package com.insurance.maintenance.service;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.ContractStatus;
import com.insurance.maintenance.domain.Customer;
import com.insurance.maintenance.domain.InsuranceProduct;
import com.insurance.maintenance.dto.response.ContractSimpleDto;
import com.insurance.maintenance.repository.ContractRepository;
import com.insurance.maintenance.repository.CustomerRepository;
import com.insurance.maintenance.repository.InsuranceProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ContractServiceTest {

    @Autowired
    private ContractService contractService;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private InsuranceProductRepository insuranceProductRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp(){
        Customer customer = Customer.builder().name("테스트고객").phoneNumber("010-1234-5678").build();
        customerRepository.save(customer);

        InsuranceProduct product = new InsuranceProduct("테스트상품");
        insuranceProductRepository.save(product);

        for(int i=0; i<5; i++){
            contractRepository.save(Contract.createContract(customer,product,"A-00"+i, ContractStatus.NORMAL));
        }
    }

    @DisplayName("계약 목록 조회 서비스(페이징)가 DTO변환을 올바르게 수행한다.")
    @Test
    void findAllContract_Paging(){
        //given
        PageRequest pageRequest = PageRequest.of(0,3);

        //when
        Page<ContractSimpleDto> dtoPage = contractService.findAllContracts(pageRequest);

        //then
        //1. 페이징 결과검증
        assertThat(dtoPage.getContent()).hasSize(3);
        assertThat(dtoPage.getTotalElements()).isEqualTo(5);
        assertThat(dtoPage.getNumber()).isEqualTo(0); // 현재 페이지번호 검증

        //2. DTO변환 검증
        assertThat(dtoPage.getContent().get(0).getCustomerName()).isEqualTo("테스트고객");
        assertThat(dtoPage.getContent().get(0).getProductName()).isEqualTo("테스트상품");
    }
}
