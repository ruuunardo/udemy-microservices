package com.teamr.runardo.cards.repository;

import com.teamr.runardo.cards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByMobileNumber(String mobileNumber);

    Optional<Card> findByCardNumber(String loanNumber);
}
