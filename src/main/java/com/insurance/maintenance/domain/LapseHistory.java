package com.insurance.maintenance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class LapseHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lapse_history_id")
    private Long id;

    // 실효 처리 기준 년월 (예: 2025-09)
    private YearMonth lapseMonth;

    private String reason; // 실효 사유

    // --- N:1 연관관계의 주인 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    // --- 생성 메소드 ---
    public static LapseHistory createLapseHistory(Contract contract, String reason) {
        LapseHistory history = new LapseHistory();
        history.contract = contract;
        history.lapseMonth = YearMonth.now(); // 현재 년월로 기록
        history.reason = reason;
        return history;
    }
}