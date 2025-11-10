package com.agiota.bank.repository;

import com.agiota.bank.model.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByNumber(String number);
}
