package com.agiota.bank.model.cards;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cvv;
}
