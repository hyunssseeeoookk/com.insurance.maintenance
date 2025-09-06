package com.insurance.maintenance.dto.response;

import com.insurance.maintenance.domain.Contract;
import lombok.Getter;

@Getter
public class SimpleContractDto {
    private Long contractId;
    private String accountNo;
    private String status;

    public SimpleContractDto(Contract contract){
        this.contractId = contract.getId();
        this.accountNo = contract.getAccountNo();
        this.status = contract.getStatus().name();
    }
}
