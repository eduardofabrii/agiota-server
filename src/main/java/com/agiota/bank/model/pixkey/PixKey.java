package com.agiota.bank.model.pixkey;

import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pix_key")
public class PixKey {
    @Id
    private String keyValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PixKeyTypes type;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;
}
