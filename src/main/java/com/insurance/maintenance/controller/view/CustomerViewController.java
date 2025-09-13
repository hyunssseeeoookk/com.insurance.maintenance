package com.insurance.maintenance.controller.view;

import com.insurance.maintenance.dto.response.CustomerDetailDto;
import com.insurance.maintenance.dto.response.CustomerDto;
import com.insurance.maintenance.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerViewController {

    private final CustomerService customerService;

    @GetMapping("/customers") // api 와 구분되는 URL
    public String customers(Model model){
        List<CustomerDto> customers = customerService.findAllCustomers();

        //Model 객체에 조회된 고객 목록을 "customers"라는 이름으로 담는다
        //이 Model이 View(thymeleaf)로 전달됨
        model.addAttribute("customers",customers);

        // "customers"라는 이름의 HTML 파일을 찾아, 렌더링하라는 의미
        return "customers"; // templates/customers.html
    }

    @GetMapping("/customers/{id}")
    public String customerDetails(@PathVariable Long id, Model model){
        CustomerDetailDto customerDetails = customerService.findCustomerDetails(id);
        model.addAttribute("customer",customerDetails);
        return "customer-detail";
    }
}
