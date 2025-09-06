package com.insurance.maintenance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InsuranceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    private String name;

    // 테스트를 위해 임시로 public 생성자 추가 (나중에 Builder 패턴으로 변경 권장)
    public InsuranceProduct(String name) {
        this.name = name;
    }
}