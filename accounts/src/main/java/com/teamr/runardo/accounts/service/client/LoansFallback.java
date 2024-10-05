package com.teamr.runardo.accounts.service.client;

import com.teamr.runardo.accounts.dto.LoanDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoansFallback implements LoansFeignClient {

    @Override
    public ResponseEntity<LoanDto> fetchCardDetails(String mobileNumber) {
        return null;
    }
}
