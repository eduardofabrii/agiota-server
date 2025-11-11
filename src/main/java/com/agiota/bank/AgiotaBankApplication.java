package com.agiota.bank;

import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.pixkey.PixKeyTypes;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.PixKeyRepository;
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
    private final PixKeyRepository pixKeyRepository;
    public static void main(String[] args) {
        SpringApplication.run(AgiotaBankApplication.class, args);
    }

    public void run(String... args) {
        User user1 = new User("Eduardo Fabri", "eduardohfabri@gmail.com", "123456", UserRole.ADMIN);
        User user2 = new User("Joao Silva", "jvitor.oliveira1803@gmail.com", "123456", UserRole.USER);
        userRepository.saveAll(Arrays.asList(user1, user2));

        List<Account> contas = getAccounts(user1, user2);
        accountRepository.saveAll(contas);
        PixKey pixKey1 = new PixKey("11111111111", PixKeyTypes.CPF ,contas.get(0));
        PixKey pixKey2 = new PixKey("41 99999-9999", PixKeyTypes.TELEFONE,contas.get(1));
        pixKeyRepository.saveAll(Arrays.asList(pixKey1, pixKey2));
    }

    private static List<Account> getAccounts(User user1, User user2) {
        Account contaCorrenteEduardo = new Account(user1, "0001", "102030-4", AccountType.CORRENTE, new BigDecimal("5000.00"));
        Account contaCorrenteJoao = new Account(user2, "0001", "405060-7", AccountType.CORRENTE, new BigDecimal("2500.00"));
        Account contaPoupancaJoao = new Account(user2, "0001", "405060-8", AccountType.POUPANCA, new BigDecimal("8000.00"));

        return Arrays.asList(contaCorrenteEduardo, contaCorrenteJoao, contaPoupancaJoao);
    }
}