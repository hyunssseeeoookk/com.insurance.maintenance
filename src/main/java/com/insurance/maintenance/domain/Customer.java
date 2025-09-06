package com.insurance.maintenance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer")
    private List<Contract> contracts = new ArrayList<>();

    @Builder
    public Customer(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
