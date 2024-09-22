package com.teamr.runardo.loans.service.impl;

import com.teamr.runardo.loans.dto.LoanDto;
import org.springframework.stereotype.Service;

@Service
public interface ILoanService {
    void createLoan(LoanDto loanDto);

    void createLoan(String mobileNumber);

    LoanDto fetchLoan(String mobileNumber);

    boolean updateLoan(LoanDto loansDto);

    boolean deleteLoan(String mobileNumber);
}
