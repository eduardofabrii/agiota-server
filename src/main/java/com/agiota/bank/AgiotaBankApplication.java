package com.agiota.bank;

import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.agiota.bank.service.notification.NotificationService;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class AgiotaBankApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(AgiotaBankApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User user1 = new User("Eduardo Fabri", "eduardohfabri@gmail.com", "1234", UserRole.ADMIN);
            User user2 = new User("Joao Silva", "joaosilva@gmail.com", "senha5678", UserRole.USER);

            List<User> usuarios = Arrays.asList(user1, user2);
            userRepository.saveAll(usuarios);
        }

        userRepository.findById(1L).ifPresent(user -> {
            String subject = "Alerta de Segurança: Transação Suspeita Detectada";
            String message = "Olá, " + user.getName() + ". Detectamos uma transação incomum de R$ 7.850,00 em sua conta. Se você não a reconhece, por favor, entre em contato conosco imediatamente.";
            notificationService.createAndSendNotification(user, subject, message);
        });
    }
}
