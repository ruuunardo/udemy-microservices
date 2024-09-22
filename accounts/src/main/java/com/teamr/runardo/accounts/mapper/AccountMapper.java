package com.teamr.runardo.accounts.mapper;


import com.teamr.runardo.accounts.dto.AccountDto;
import com.teamr.runardo.accounts.entity.Account;

public class AccountMapper {

    public static AccountDto mapToAccountDto(Account account, AccountDto accountDto) {
        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBranchAddress(account.getBranchAddress());
        return accountDto;
    }

    public static Account mapToAccount(AccountDto AccountDto, Account Account) {
        Account.setAccountNumber(AccountDto.getAccountNumber());
        Account.setAccountType(AccountDto.getAccountType());
        Account.setBranchAddress(AccountDto.getBranchAddress());
        return Account;
    }

}