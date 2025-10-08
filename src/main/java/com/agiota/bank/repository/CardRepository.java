package com.agiota.bank.repository;

import com.agiota.bank.model.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
