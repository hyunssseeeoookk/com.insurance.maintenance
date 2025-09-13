package com.insurance.maintenance.controller.api;

import com.insurance.maintenance.dto.response.ContractSimpleDto;
import com.insurance.maintenance.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractApiController {
    private final ContractService contractService;

    @GetMapping
    public ResponseEntity<Page<ContractSimpleDto>> getContracts(@PageableDefault(size=10, sort="id")Pageable pageable){
        Page<ContractSimpleDto> contractPage = contractService.findAllContracts(pageable);
        return ResponseEntity.ok(contractPage);
    }
}
