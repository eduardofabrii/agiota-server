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
        User user1 = new User();
        user1.setName("Eduardo Fabri");
        user1.setEmail("eduardohfabri@gmail.com");
        user1.setPassword("1234");
        user1.setRole(UserRole.ADMIN);

        User user2 = new User();
        user2.setName("Joao Silva");
        user2.setEmail("joaosilva@gmail.com");
        user2.setPassword("senha5678");
        user2.setRole(UserRole.USER);

        List<User> usuarios = Arrays.asList(
                user1,
                user2
        );

        userRepository.saveAll(usuarios);
    }
}
