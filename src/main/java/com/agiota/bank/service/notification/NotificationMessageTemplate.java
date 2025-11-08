package com.agiota.bank.service.notification;

import com.agiota.bank.model.notification.NotificationType;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NotificationMessageTemplate {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static NotificationMessage createAccountMessage(String accountNumber, String agency) {
        String subject = "Conta Criada com Sucesso - Agiota Bank";
        String message = String.format(
                "Parabéns! Sua conta foi criada com sucesso.\n\n" +
                        "Detalhes da conta:\n" +
                        "• Agência: %s\n" +
                        "• Conta: %s\n\n" +
                        "Agora você pode começar a usar todos os serviços do Agiota Bank.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                agency, accountNumber
        );
        return new NotificationMessage(subject, message, NotificationType.ACCOUNT_CREATED);
    }

    public static NotificationMessage updateAccountMessage(String accountNumber) {
        String subject = "Conta Atualizada - Agiota Bank";
        String message = String.format(
                "Sua conta %s foi atualizada com sucesso.\n\n" +
                        "Se você não fez esta alteração, entre em contato conosco imediatamente.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                accountNumber
        );
        return new NotificationMessage(subject, message, NotificationType.ACCOUNT_UPDATED);
    }

    public static NotificationMessage deleteAccountMessage(String accountNumber) {
        String subject = "Conta Excluída - Agiota Bank";
        String message = String.format(
                "Sua conta %s foi excluída com sucesso.\n\n" +
                        "Lamentamos vê-lo partir. Se precisar de ajuda, estamos sempre aqui.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                accountNumber
        );
        return new NotificationMessage(subject, message, NotificationType.ACCOUNT_DELETED);
    }

    public static NotificationMessage createPixKeyMessage(String pixKey, String keyType) {
        String subject = "Chave PIX Criada - Agiota Bank";
        String message = String.format(
                "Sua chave PIX foi criada com sucesso!\n\n" +
                        "Detalhes da chave PIX:\n" +
                        "• Tipo: %s\n" +
                        "• Chave: %s\n\n" +
                        "Agora você pode receber transferências PIX usando esta chave.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                keyType, pixKey
        );
        return new NotificationMessage(subject, message, NotificationType.PIX_KEY_CREATED);
    }

    public static NotificationMessage deletePixKeyMessage(String pixKey) {
        String subject = "Chave PIX Excluída - Agiota Bank";
        String message = String.format(
                "Sua chave PIX %s foi excluída com sucesso.\n\n" +
                        "Você não poderá mais receber transferências usando esta chave.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                pixKey
        );
        return new NotificationMessage(subject, message, NotificationType.PIX_KEY_DELETED);
    }

    public static NotificationMessage transactionSentMessage(double amount, String destinationInfo, String transactionType) {
        String subject = "Transação Enviada - Agiota Bank";
        String message = String.format(
                "Sua transação foi processada com sucesso!\n\n" +
                        "Detalhes da transação:\n" +
                        "• Tipo: %s\n" +
                        "• Valor: %s\n" +
                        "• Destino: %s\n" +
                        "• Data: %s\n\n" +
                        "O valor foi debitado da sua conta.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                transactionType,
                CURRENCY_FORMATTER.format(amount),
                destinationInfo,
                LocalDateTime.now().format(DATE_FORMATTER)
        );
        return new NotificationMessage(subject, message, NotificationType.TRANSACTION_SENT);
    }

    public static NotificationMessage transactionReceivedMessage(double amount, String originInfo, String transactionType) {
        String subject = "Transação Recebida - Agiota Bank";
        String message = String.format(
                "Você recebeu uma nova transação!\n\n" +
                        "Detalhes da transação:\n" +
                        "• Tipo: %s\n" +
                        "• Valor: %s\n" +
                        "• Origem: %s\n" +
                        "• Data: %s\n\n" +
                        "O valor foi creditado na sua conta.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                transactionType,
                CURRENCY_FORMATTER.format(amount),
                originInfo,
                LocalDateTime.now().format(DATE_FORMATTER)
        );
        return new NotificationMessage(subject, message, NotificationType.TRANSACTION_RECEIVED);
    }

    public static NotificationMessage createCardMessage(String cardNumber, String cardType) {
        String maskedCardNumber = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        
        String subject = "Cartão Criado - Agiota Bank";
        String message = String.format(
                "Seu novo cartão foi criado com sucesso!\n\n" +
                        "Detalhes do cartão:\n" +
                        "• Tipo: %s\n" +
                        "• Número: %s\n\n" +
                        "Seu cartão já está ativo e pronto para uso.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                cardType, maskedCardNumber
        );
        return new NotificationMessage(subject, message, NotificationType.CARD_CREATED);
    }

    public static NotificationMessage updateCardMessage(String maskedCardNumber) {
        String subject = "Cartão Atualizado - Agiota Bank";
        String message = String.format(
                "Seu cartão %s foi atualizado com sucesso.\n\n" +
                        "Se você não fez esta alteração, entre em contato conosco imediatamente.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                maskedCardNumber
        );
        return new NotificationMessage(subject, message, NotificationType.CARD_UPDATED);
    }

    public static NotificationMessage deleteCardMessage(String maskedCardNumber) {
        String subject = "Cartão Excluído - Agiota Bank";
        String message = String.format(
                "Seu cartão %s foi excluído com sucesso.\n\n" +
                        "O cartão não poderá mais ser utilizado para transações.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                maskedCardNumber
        );
        return new NotificationMessage(subject, message, NotificationType.CARD_DELETED);
    }

    public static NotificationMessage addBeneficiaryMessage(String beneficiaryName) {
        String subject = "Beneficiário Adicionado - Agiota Bank";
        String message = String.format(
                "O beneficiário %s foi adicionado com sucesso à sua lista.\n\n" +
                        "Agora você pode fazer transferências mais rapidamente para este contato.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                beneficiaryName
        );
        return new NotificationMessage(subject, message, NotificationType.BENEFICIARY_ADDED);
    }

    public static NotificationMessage updateBeneficiaryMessage(String beneficiaryName) {
        String subject = "Beneficiário Atualizado - Agiota Bank";
        String message = String.format(
                "Os dados do beneficiário %s foram atualizados com sucesso.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                beneficiaryName
        );
        return new NotificationMessage(subject, message, NotificationType.BENEFICIARY_UPDATED);
    }

    public static NotificationMessage deleteBeneficiaryMessage(String beneficiaryName) {
        String subject = "Beneficiário Removido - Agiota Bank";
        String message = String.format(
                "O beneficiário %s foi removido da sua lista com sucesso.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Agiota Bank",
                beneficiaryName
        );
        return new NotificationMessage(subject, message, NotificationType.BENEFICIARY_DELETED);
    }

    public static NotificationMessage securityAlertMessage(String action) {
        String subject = "Alerta de Segurança - Agiota Bank";
        String message = String.format(
                "ALERTA DE SEGURANÇA\n\n" +
                        "Detectamos a seguinte atividade em sua conta:\n" +
                        "• Ação: %s\n" +
                        "• Data: %s\n\n" +
                        "Se você não reconhece esta atividade, entre em contato conosco imediatamente.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe de Segurança Agiota Bank",
                action,
                LocalDateTime.now().format(DATE_FORMATTER)
        );
        return new NotificationMessage(subject, message, NotificationType.SECURITY_ALERT);
    }

    // Classe interna para encapsular a mensagem e tipo
    public static class NotificationMessage {
        private final String subject;
        private final String message;
        private final NotificationType type;

        public NotificationMessage(String subject, String message, NotificationType type) {
            this.subject = subject;
            this.message = message;
            this.type = type;
        }

        public String getSubject() {
            return subject;
        }

        public String getMessage() {
            return message;
        }

        public NotificationType getType() {
            return type;
        }
    }
}