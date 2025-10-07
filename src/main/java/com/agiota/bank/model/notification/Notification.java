// src/main/java/com/agiota/bank/model/notification/Notification.java

package com.agiota.bank.model.notification;

import com.agiota.bank.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient; 

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isRead = false;

    /**
     * Construtor customizado para facilitar a criação de novas notificações
     * sem a necessidade de passar todos os campos.
     */
    public Notification(User recipient, String message) {
        this.recipient = recipient;
        this.message = message;
        this.isRead = false; 
    }
}