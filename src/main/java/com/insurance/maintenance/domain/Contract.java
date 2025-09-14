package com.insurance.maintenance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="contract_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNo;   // 증권번호

    @Enumerated(EnumType.STRING)
    private ContractStatus status;  // 계약상태 Enum

    // N:1 연관관계의 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private InsuranceProduct product;

    // 계약이 되면, 관련된 모든 납입이력도 함께 삭제되도록 Cascade설정
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    // --- 생성 메소드 ---
    public static Contract createContract(Customer customer, InsuranceProduct product, String accountNo, ContractStatus status) {
        Contract contract = new Contract();
        contract.product = product;
        contract.accountNo = accountNo;
        contract.status = status;
        contract.setCustomer(customer);
        return contract;
    }

    // --- 연관관계 편의 메소드 ---
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.getContracts().add(this);
    }
}
