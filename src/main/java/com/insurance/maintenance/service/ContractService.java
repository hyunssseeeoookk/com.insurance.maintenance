package com.insurance.maintenance.service;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.dto.response.ContractSimpleDto;
import com.insurance.maintenance.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    public Page<ContractSimpleDto> findAllContracts(Pageable pageable){
        // 1. Repository를 호출하여 페이징된 엔티티 데이터를 조회한다.
        Page<Contract> contractPage = contractRepository.findAllWithDetails(pageable);

        // 2. Page 객체의 map 기능을 사용하여 엔티티 페이지를 DTO로 변환한다.
        return contractPage.map(ContractSimpleDto::new);
    }
}
