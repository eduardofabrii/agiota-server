package com.agiota.bank.model.beneficiary;

import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "beneficiary")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account ownerAccount;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpfCnpj;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String agency;

    @Column(nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Beneficiary(Account ownerAccount, String name, String cpfCnpj, String bankCode, 
                      String agency, String accountNumber, AccountType accountType) {
        this.ownerAccount = ownerAccount;
        this.name = name;
        this.cpfCnpj = cpfCnpj;
        this.bankCode = bankCode;
        this.agency = agency;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }
}