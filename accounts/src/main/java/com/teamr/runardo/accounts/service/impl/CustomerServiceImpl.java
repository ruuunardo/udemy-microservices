package com.teamr.runardo.accounts.service.impl;

import com.teamr.runardo.accounts.dto.AccountDto;
import com.teamr.runardo.accounts.dto.CardDto;
import com.teamr.runardo.accounts.dto.CustomerDetailsDto;
import com.teamr.runardo.accounts.dto.LoanDto;
import com.teamr.runardo.accounts.entity.Account;
import com.teamr.runardo.accounts.entity.Customer;
import com.teamr.runardo.accounts.exception.ResourceNotFoundException;
import com.teamr.runardo.accounts.mapper.AccountMapper;
import com.teamr.runardo.accounts.mapper.CustomerMapper;
import com.teamr.runardo.accounts.respository.AccountRepository;
import com.teamr.runardo.accounts.respository.CustomerRepository;
import com.teamr.runardo.accounts.service.ICustomerService;
import com.teamr.runardo.accounts.service.client.CardsFeignClient;
import com.teamr.runardo.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;


    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("customer", "mobileNumber", mobileNumber)
        );

        Account account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountMapper.mapToAccountDto(account, new AccountDto()));

        ResponseEntity<LoanDto> loanDtoResponseEntity = loansFeignClient.fetchCardDetails(mobileNumber);
        customerDetailsDto.setLoanDto(loanDtoResponseEntity.getBody());

        ResponseEntity<CardDto> cardDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);
        customerDetailsDto.setCardDto(cardDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}
