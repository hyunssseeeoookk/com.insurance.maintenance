package com.insurance.maintenance.controller.view;

import com.insurance.maintenance.domain.Contract;
import com.insurance.maintenance.dto.response.ContractSimpleDto;
import com.insurance.maintenance.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ContractViewController {

    private final ContractService contractService;

    @GetMapping("/contracts")
    public String contracts(@PageableDefault(size=5, sort = "id")Pageable pageable, Model model){
        Page<ContractSimpleDto> contractsPage = contractService.findAllContracts(pageable);
        model.addAttribute("contractsPage", contractsPage);
        return "contracts";
    }


}
