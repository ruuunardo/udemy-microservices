package com.teamr.runardo.cards.service;

import com.teamr.runardo.cards.dto.CardDto;
import org.springframework.stereotype.Service;

@Service
public interface ICardService {
    void createCard(String mobileNumber);

    CardDto fetchCard(String mobileNumber);

    boolean updateCard(CardDto loansDto);

    boolean deleteCard(String mobileNumber);

}
