package com.insurance.maintenance.dto.response;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.domain.ContractStatus;
import lombok.Getter;

/**
 * 고객 상세 조회 시, 내부에 포함될 계약의 요약 정보를 담는 DTO
 */
@Getter
public class ContractSimpleDto {
    private Long contractId;
    private String accountNo;
    private ContractStatus status;
    private String customerName;
    private String productName;

    public ContractSimpleDto(Contract contract) {
        this.contractId = contract.getId();
        this.accountNo = contract.getAccountNo();
        this.status = contract.getStatus();
        this.customerName = contract.getCustomer().getName();
        this.productName = contract.getProduct().getName();
    }
}
