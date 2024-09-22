package com.teamr.runardo.loans.service;

import com.teamr.runardo.loans.constants.LoansConstants;
import com.teamr.runardo.loans.dto.LoanDto;
import com.teamr.runardo.loans.entity.Loan;
import com.teamr.runardo.loans.exception.LoanAlreadyExistsException;
import com.teamr.runardo.loans.exception.ResourceNotFoundException;
import com.teamr.runardo.loans.mapper.LoanMapper;
import com.teamr.runardo.loans.repository.LoanRepository;
import com.teamr.runardo.loans.service.impl.ILoanService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class LoanServiceImpl implements ILoanService {
    private LoanRepository loanRepository;

    @Override
    public void createLoan(LoanDto loanDto) {
        Loan loan = LoanMapper.mapToloan(loanDto, new Loan());
        loanRepository.save(loan);
    }

    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loan> loan = loanRepository.findByMobileNumber(mobileNumber);
        if (loan.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already registered with given mobileNumber " + mobileNumber);
        }
        loanRepository.save(createNewLoan(mobileNumber));
    }

    private Loan createNewLoan(String mobileNumber) {
        Loan newLoan = new Loan();
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return newLoan;
    }

    @Override
    public LoanDto fetchLoan(String mobileNumber) {
        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("loan", "mobileNumber", mobileNumber)
        );
        LoanDto loanDto = LoanMapper.mapToloanDto(loan, new LoanDto());

        return loanDto;
    }

    @Override
    public boolean updateLoan(LoanDto loansDto) {
        boolean isUpdated = false;
        Loan loan = loanRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(
                () -> new ResourceNotFoundException("loan", "loan number", loansDto.getLoanNumber())
        );

        LoanMapper.mapToloan(loansDto, loan);
        loanRepository.save(loan);
        isUpdated = true;
        return isUpdated;
    }

    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loan loan = loanRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("loan", "mobileNumber", mobileNumber)
        );
        loanRepository.delete(loan);
        return true;
    }

}
