package com.teamr.runardo.cards.service.impl;

import com.teamr.runardo.cards.constants.CardConstants;
import com.teamr.runardo.cards.dto.CardDto;
import com.teamr.runardo.cards.entity.Card;
import com.teamr.runardo.cards.exception.CardAlreadyExistsException;
import com.teamr.runardo.cards.exception.ResourceNotFoundException;
import com.teamr.runardo.cards.mapper.CardMapper;
import com.teamr.runardo.cards.repository.CardRepository;
import com.teamr.runardo.cards.service.ICardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardServiceImpl implements ICardService {
    private CardRepository cardRepository;

    @Override
    public void createCard(String mobileNumber) {
        Optional<Card> card = cardRepository.findByMobileNumber(mobileNumber);
        if (card.isPresent()) {
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber " + mobileNumber);
        }
        cardRepository.save(createNewCard(mobileNumber));
    }

    private Card createNewCard(String mobileNumber) {
        Card newCard = new Card();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardConstants.NEW_CARD_LIMIT);
        return newCard;
    }

    @Override
    public CardDto fetchCard(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        CardDto cardDto = CardMapper.mapToCardDto(card, new CardDto());

        return cardDto;
    }

    @Override
    public boolean updateCard(CardDto cardDto) {
        boolean isUpdated = false;
        Card card = cardRepository.findByCardNumber(cardDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("card", "card number", cardDto.getCardNumber())
        );

        CardMapper.mapToCardDto(card, cardDto);
        cardRepository.save(card);
        isUpdated = true;
        return isUpdated;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Card card = cardRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("card", "mobileNumber", mobileNumber)
        );
        cardRepository.delete(card);
        return true;
    }

}
