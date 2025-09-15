package com.insurance.maintenance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_id")
    private Long id;

    private LocalDate paymentDate; // 납입일
    private BigDecimal amount; // 납입금액
    private Integer paymentSequence; // 납입회차

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    // 테스트 및 Processor에서 사용할 생성자
    public Payment(LocalDate paymentDate, BigDecimal amount, Integer paymentSequence, Contract contract){
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentSequence = paymentSequence;
        this.contract = contract;
    }
}
