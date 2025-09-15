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

    // 매월 납입 기일 (1 ~ 31)
    @Column(nullable = false)
    private int paymentDueDate;

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

    // 1:N관계
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<LapseHistory> lapseHistories = new ArrayList<>();

    /**
     * 계약상태를 '실효'로 변경하고, 실효'이력'을 생성
     * @param reason
     */
    public void toLapse(String reason){
        if(this.status != ContractStatus.LAPSE_NOTICE){
            throw new IllegalStateException("실효 예고 상태의 계약만 실효처리 가능");
        }
        this.status = ContractStatus.LAPSE;
        // 연관관계 편의메서드를 통해 실효이력 추가
        this.addLapseHistory(reason);
    }

    public void reinstate(){
        if(this.status != ContractStatus.LAPSE){
            throw new IllegalStateException("실효상태의 계약만 부활처리 가능");
        }
        this.status = ContractStatus.NORMAL;
        // 부활 시 별도의 '부활이력' 엔티티 만들어 기록 필요
    }

    /**
     * 연관관계 편의메서드
     * @param reason
     */
    private void addLapseHistory(String reason) {
        LapseHistory history = LapseHistory.createLapseHistory(this,reason);
        this.lapseHistories.add(history);
    }

    // --- 생성 메소드 ---
    public static Contract createContract(Customer customer, InsuranceProduct product, String accountNo, ContractStatus status, int paymentDueDate) {
        Contract contract = new Contract();
        contract.product = product;
        contract.accountNo = accountNo;
        contract.status = status;
        contract.paymentDueDate = paymentDueDate; // 생성 시 납입일 지정
        contract.setCustomer(customer);
        return contract;
    }

    // --- 연관관계 편의 메소드 ---
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.getContracts().add(this);
    }
}
