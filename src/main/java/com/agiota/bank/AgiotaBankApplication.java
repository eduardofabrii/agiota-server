package com.agiota.bank;

import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class AgiotaBankApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(AgiotaBankApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println(">>> Banco de dados vazio. Criando registros de teste... <<<");

        User user1 = new User("Eduardo Fabri", "eduardohfabri@gmail.com", "123456", UserRole.ADMIN);
        User user2 = new User("Joao Silva", "joaosilva@gmail.com", "123456", UserRole.USER);
        userRepository.saveAll(Arrays.asList(user1, user2));

        List<Account> contas = getAccounts(user1, user2);
        accountRepository.saveAll(contas);

        System.out.println(">>> Usuários e contas de teste criados com sucesso! <<<");
    }

    private static List<Account> getAccounts(User user1, User user2) {
        Account contaCorrenteEduardo = new Account(user1, "0001", "102030-4", AccountType.CORRENTE, new BigDecimal("5000.00"));
        Account contaCorrenteJoao = new Account(user2, "0001", "405060-7", AccountType.CORRENTE, new BigDecimal("2500.00"));
        Account contaPoupancaJoao = new Account(user2, "0001", "405060-8", AccountType.POUPANCA, new BigDecimal("8000.00"));

        return Arrays.asList(contaCorrenteEduardo, contaCorrenteJoao, contaPoupancaJoao);
    }
}