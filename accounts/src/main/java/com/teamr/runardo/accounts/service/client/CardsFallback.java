package com.teamr.runardo.accounts.service.client;

import com.teamr.runardo.accounts.dto.CardDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CardsFallback implements CardsFeignClient{
    @Override
    public ResponseEntity<CardDto> fetchCardDetails(String mobileNumber) {
        return null;
    }
}
