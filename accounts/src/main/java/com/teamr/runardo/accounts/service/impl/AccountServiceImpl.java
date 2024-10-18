package com.teamr.runardo.accounts.service.impl;

import com.teamr.runardo.accounts.constants.AccountsConstants;
import com.teamr.runardo.accounts.dto.AccountDto;
import com.teamr.runardo.accounts.dto.AccountsMsgDto;
import com.teamr.runardo.accounts.dto.CustomerDto;
import com.teamr.runardo.accounts.entity.Account;
import com.teamr.runardo.accounts.entity.Customer;
import com.teamr.runardo.accounts.exception.CustomerAlreadyExistsException;
import com.teamr.runardo.accounts.exception.ResourceNotFoundException;
import com.teamr.runardo.accounts.mapper.AccountMapper;
import com.teamr.runardo.accounts.mapper.CustomerMapper;
import com.teamr.runardo.accounts.respository.AccountRepository;
import com.teamr.runardo.accounts.respository.CustomerRepository;
import com.teamr.runardo.accounts.service.IAccountService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> findCustomer = customerRepository.findByMobileNumber(customer.getMobileNumber());
        if (findCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile Number" + customer.getMobileNumber());
        }
//        customer.setCreatedAt(LocalDateTime.now());
//        customer.setCreatedBy("Anonymous");

        Customer savedCustomer = customerRepository.save(customer);
        Account savedAccount = accountRepository.save(createNewAccount(customer));
    }

    private void sendCommunication(Account account, Customer customer) {
        var accountsMsgDto = new AccountsMsgDto(account.getAccountNumber(), customer.getName(),
                customer.getEmail(), customer.getMobileNumber());
        log.info("Sending Communication request for the details: {}", accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Account createNewAccount(Customer customer) {
        Account newAccount = new Account();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
//        newAccount.setCreatedAt(LocalDateTime.now());
//        newAccount.setCreatedBy("Anonymous");
        return newAccount;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Optional<Customer> customer = customerRepository.findByMobileNumber(mobileNumber);
        if (customer.isPresent()) {
            Optional<Account> account = accountRepository.findByCustomerId(customer.get().getCustomerId());
            CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer.get(), new CustomerDto());
            if (account.isPresent()) {
                customerDto.setAccountsDto(AccountMapper.mapToAccountDto(account.get(), new AccountDto()));
            } else {
                throw new ResourceNotFoundException("Account", "customerId", String.valueOf(customer.get().getCustomerId()));
            }
            return customerDto;
        }
        throw new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber);
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Account account = accountRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "account number", accountsDto.getAccountNumber().toString())
            );
            AccountMapper.mapToAccount(accountsDto, account);
            account = accountRepository.save(account);

            Long customerId = account.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "customer id", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        boolean isDelete = false;
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobile number", mobileNumber)
        );

        accountRepository.findByCustomerId(customer.getCustomerId()).ifPresent(a -> accountRepository.delete(a));
        customerRepository.delete(customer);
        isDelete = true;

        return isDelete;
    }

    /**
     * @param accountNumber - Long
     * @return
     */
    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if(accountNumber !=null ){
            Account accounts = accountRepository.findById(accountNumber).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", accountNumber.toString())
            );
            accounts.setCommunicationSw(true);
            accountRepository.save(accounts);
            isUpdated = true;
        }
        return  isUpdated;
    }

}
