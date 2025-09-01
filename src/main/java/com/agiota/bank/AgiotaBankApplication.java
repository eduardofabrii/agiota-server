package com.agiota.bank;

import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class AgiotaBankApplication implements CommandLineRunner {

    private final UserRepository userRepository;

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
    }
}
