package com.agiota.bank.model.cards;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;
    private String holderName;
    private String expirationDate;
    private String cvv;
}
