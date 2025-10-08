package com.agiota.bank.model.pixkey;

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
    private PixKeyTypes type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User owner;
}
