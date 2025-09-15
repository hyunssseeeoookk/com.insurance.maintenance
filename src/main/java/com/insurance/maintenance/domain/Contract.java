package com.insurance.maintenance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private String accountNo;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(nullable = false)
    private int paymentDueDate;

    private LocalDate lastPaymentDate;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id")
    private InsuranceProduct product;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<LapseHistory> lapseHistories = new ArrayList<>();

    public static Contract createContract(Customer c, InsuranceProduct p, String a, ContractStatus s, int d) {
        Contract contract = new Contract();
        contract.product = p;
        contract.accountNo = a;
        contract.status = s;
        contract.paymentDueDate = d;
        contract.setCustomer(c);
        if (s == ContractStatus.NORMAL) {
            contract.lastPaymentDate = LocalDate.now();
        }
        return contract;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if(customer != null) {
            customer.getContracts().add(this);
        }
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
        this.lastPaymentDate = LocalDate.from(payment.getPaymentDate());
    }

    public void toLapse(String reason){
        if(this.status != ContractStatus.NORMAL){
            throw new IllegalStateException("정상 상태의 계약만 실효 처리할 수 있습니다.");
        }
        this.status = ContractStatus.LAPSE;
        this.addLapseHistory(reason);
    }

    public void reinstate(){
        if(this.status != ContractStatus.LAPSE){
            throw new IllegalStateException("실효 상태의 계약만 부활 처리할 수 있습니다.");
        }
        this.status = ContractStatus.NORMAL;
    }

    private void addLapseHistory(String reason) {
        LapseHistory history = LapseHistory.createLapseHistory(this,reason);
        this.lapseHistories.add(history);
    }

    // 테스트 코드의 편의를 위한 메소드
    public void updateLastPaymentDateForTest(LocalDate date) {
        this.lastPaymentDate = date;
    }
}
