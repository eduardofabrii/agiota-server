package com.agiota.bank.repository;

import com.agiota.bank.model.cards.Cards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Cards, Long> {
}
