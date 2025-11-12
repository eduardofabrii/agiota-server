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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient; 

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean isRead = false;

    /**
     * Construtor customizado para facilitar a criação de novas notificações
     * sem a necessidade de passar todos os campos.
     */
    public Notification(User recipient, String message, NotificationType type) {
        this.recipient = recipient;
        this.message = message;
        this.type = type;
        this.isRead = false; 
        this.createdAt = LocalDateTime.now();
    }

 
    public Notification(User recipient, String message) {
        this(recipient, message, NotificationType.SYSTEM_MAINTENANCE);
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}