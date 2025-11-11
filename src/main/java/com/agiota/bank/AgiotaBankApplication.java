package com.agiota.bank;

import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.model.beneficiary.Beneficiary;
import com.agiota.bank.model.cards.Card;
import com.agiota.bank.model.device.AuthorizedDevice;
import com.agiota.bank.model.device.DeviceType;
import com.agiota.bank.model.loan.Loan;
import com.agiota.bank.model.loan.LoanStatus;
import com.agiota.bank.model.notification.Notification;
import com.agiota.bank.model.notification.NotificationType;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.pixkey.PixKeyTypes;
import com.agiota.bank.model.statement.BankStatement;
import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.statement.StatementType;
import com.agiota.bank.model.supportticket.SupportTicket;
import com.agiota.bank.model.supportticket.SupportTicketStatus;
import com.agiota.bank.model.transaction.Transaction;
import com.agiota.bank.model.transaction.TransactionType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.AuthorizedDeviceRepository;
import com.agiota.bank.repository.BankStatementRepository;
import com.agiota.bank.repository.BeneficiaryRepository;
import com.agiota.bank.repository.CardRepository;
import com.agiota.bank.repository.LoanRepository;
import com.agiota.bank.repository.NotificationRepository;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.repository.SupportTicketRepository;
import com.agiota.bank.repository.TransactionRepository;
import com.agiota.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class AgiotaBankApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;
    private final TransactionRepository transactionRepository;
    private final BankStatementRepository bankStatementRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final CardRepository cardRepository;
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final AuthorizedDeviceRepository authorizedDeviceRepository;

    public static void main(String[] args) {
        SpringApplication.run(AgiotaBankApplication.class, args);
    }

    public void run(String... args) {
        // Criando usuários com diferentes perfis e tempo de uso
        User user1 = createUser("Maria Silva", "maria.silva@email.com", "123456", UserRole.ADMIN, 180); // 6 meses
        User user2 = createUser("Carlos Santos", "carlos.santos@email.com", "123456", UserRole.USER, 90); // 3 meses
        User user3 = createUser("Ana Costa", "ana.costa@email.com", "123456", UserRole.USER, 30); // 1 mês
        userRepository.saveAll(Arrays.asList(user1, user2, user3));

        // Criando contas para os usuários
        List<Account> contas = createAccounts(user1, user2, user3);
        accountRepository.saveAll(contas);

        // Criando chaves PIX para as contas
        List<PixKey> pixKeys = createPixKeys(contas);
        pixKeyRepository.saveAll(pixKeys);

        // Criando transações históricas
        List<Transaction> transactions = createTransactions(contas);
        transactionRepository.saveAll(transactions);

        // Criando extratos bancários
        List<BankStatement> statements = createBankStatements(contas);
        bankStatementRepository.saveAll(statements);

        // Criando beneficiários
        List<Beneficiary> beneficiaries = createBeneficiaries(contas);
        beneficiaryRepository.saveAll(beneficiaries);

        // Criando cartões
        List<Card> cards = createCards(contas);
        cardRepository.saveAll(cards);

        // Criando empréstimos
        List<Loan> loans = createLoans(contas);
        loanRepository.saveAll(loans);

        // Criando notificações
        List<Notification> notifications = createNotifications(Arrays.asList(user1, user2, user3), contas);
        notificationRepository.saveAll(notifications);

        // Criando tickets de suporte
        List<SupportTicket> tickets = createSupportTickets(Arrays.asList(user1, user2, user3));
        supportTicketRepository.saveAll(tickets);

        // Criando dispositivos autorizados
        List<AuthorizedDevice> devices = createAuthorizedDevices(Arrays.asList(user1, user2, user3));
        authorizedDeviceRepository.saveAll(devices);

        System.out.println("==================================================");
        System.out.println("✅ Banco de dados populado com sucesso!");
        System.out.println("==================================================");
        System.out.println("👥 Usuários criados: " + userRepository.count());
        System.out.println("💳 Contas criadas: " + accountRepository.count());
        System.out.println("🔑 Chaves PIX criadas: " + pixKeyRepository.count());
        System.out.println("💸 Transações criadas: " + transactionRepository.count());
        System.out.println("📄 Extratos criados: " + bankStatementRepository.count());
        System.out.println("👤 Beneficiários criados: " + beneficiaryRepository.count());
        System.out.println("💳 Cartões criados: " + cardRepository.count());
        System.out.println("💰 Empréstimos criados: " + loanRepository.count());
        System.out.println("🔔 Notificações criadas: " + notificationRepository.count());
        System.out.println("🎫 Tickets de suporte criados: " + supportTicketRepository.count());
        System.out.println("📱 Dispositivos autorizados: " + authorizedDeviceRepository.count());
        System.out.println("==================================================");
    }

    private User createUser(String name, String email, String password, UserRole role, int daysAgo) {
        User user = new User(name, email, password, role);
        // Simula que o usuário foi criado há X dias
        try {
            var field = User.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(user, LocalDateTime.now().minusDays(daysAgo));
        } catch (Exception e) {
            // Se não conseguir setar, usa a data atual
        }
        return user;
    }

    private List<Account> createAccounts(User user1, User user2, User user3) {
        List<Account> accounts = new ArrayList<>();

        // Contas da Maria (usuária mais antiga - 6 meses)
        Account mariaCorrente = new Account(user1, "0001", "1001001", AccountType.CORRENTE, new BigDecimal("15750.80"));
        mariaCorrente.setStatus(AccountStatus.ATIVO);
        accounts.add(mariaCorrente);

        Account mariaPoupanca = new Account(user1, "0001", "1001002", AccountType.POUPANCA, new BigDecimal("25000.00"));
        mariaPoupanca.setStatus(AccountStatus.ATIVO);
        accounts.add(mariaPoupanca);

        // Contas do Carlos (usuário médio - 3 meses)
        Account carlosCorrente = new Account(user2, "0001", "2002001", AccountType.CORRENTE, new BigDecimal("8450.50"));
        carlosCorrente.setStatus(AccountStatus.ATIVO);
        accounts.add(carlosCorrente);

        Account carlosEmpresarial = new Account(user2, "0001", "2002002", AccountType.EMPRESARIAL, new BigDecimal("32500.00"));
        carlosEmpresarial.setStatus(AccountStatus.ATIVO);
        accounts.add(carlosEmpresarial);

        // Contas da Ana (usuária nova - 1 mês)
        Account anaCorrente = new Account(user3, "0001", "3003001", AccountType.CORRENTE, new BigDecimal("3200.00"));
        anaCorrente.setStatus(AccountStatus.ATIVO);
        accounts.add(anaCorrente);

        Account anaPoupanca = new Account(user3, "0001", "3003002", AccountType.POUPANCA, new BigDecimal("5000.00"));
        anaPoupanca.setStatus(AccountStatus.ATIVO);
        accounts.add(anaPoupanca);

        return accounts;
    }

    private List<PixKey> createPixKeys(List<Account> accounts) {
        List<PixKey> pixKeys = new ArrayList<>();

        // Chaves PIX para Maria
        pixKeys.add(new PixKey("111.222.333-44", PixKeyTypes.CPF, accounts.get(0)));
        pixKeys.add(new PixKey("maria.silva@email.com", PixKeyTypes.EMAIL, accounts.get(0)));
        pixKeys.add(new PixKey("41987654321", PixKeyTypes.TELEFONE, accounts.get(1)));

        // Chaves PIX para Carlos
        pixKeys.add(new PixKey("555.666.777-88", PixKeyTypes.CPF, accounts.get(2)));
        pixKeys.add(new PixKey("41991234567", PixKeyTypes.TELEFONE, accounts.get(2)));
        pixKeys.add(new PixKey("carlos.santos@email.com", PixKeyTypes.EMAIL, accounts.get(3)));

        // Chaves PIX para Ana
        pixKeys.add(new PixKey("999.888.777-66", PixKeyTypes.CPF, accounts.get(4)));
        pixKeys.add(new PixKey("ana.costa@email.com", PixKeyTypes.EMAIL, accounts.get(4)));

        return pixKeys;
    }

    private List<Transaction> createTransactions(List<Account> accounts) {
        List<Transaction> transactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Transações da Maria (conta mais antiga - 6 meses de histórico)
        // Mês 1 - há 180 dias
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("5000.00"),
            TransactionType.PIX, now.minusDays(180), "Depósito inicial"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(2), new BigDecimal("500.00"),
            TransactionType.PIX, now.minusDays(175), "Pagamento freelance"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("3000.00"),
            TransactionType.TED, now.minusDays(170), "Salário"));

        // Mês 2 - há 150 dias
        transactions.add(createTransaction(accounts.get(0), accounts.get(2), new BigDecimal("800.00"),
            TransactionType.PIX, now.minusDays(150), "Aluguel"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(3), new BigDecimal("1200.00"),
            TransactionType.TED, now.minusDays(145), "Pagamento serviço"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("3000.00"),
            TransactionType.TED, now.minusDays(140), "Salário"));

        // Mês 3 - há 120 dias
        transactions.add(createTransaction(accounts.get(0), accounts.get(1), new BigDecimal("5000.00"),
            TransactionType.PIX, now.minusDays(120), "Transferência para poupança"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("3500.00"),
            TransactionType.TED, now.minusDays(110), "Salário"));

        // Mês 4 - há 90 dias
        transactions.add(createTransaction(accounts.get(0), accounts.get(4), new BigDecimal("300.00"),
            TransactionType.PIX, now.minusDays(90), "Presente aniversário"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("3500.00"),
            TransactionType.TED, now.minusDays(80), "Salário"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(2), new BigDecimal("850.00"),
            TransactionType.PIX, now.minusDays(75), "Aluguel"));

        // Mês 5 - há 60 dias
        transactions.add(createTransaction(accounts.get(0), accounts.get(1), new BigDecimal("2000.00"),
            TransactionType.PIX, now.minusDays(60), "Investimento poupança"));
        transactions.add(createTransaction(accounts.get(2), accounts.get(0), new BigDecimal("500.00"),
            TransactionType.PIX, now.minusDays(55), "Pagamento projeto"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("3500.00"),
            TransactionType.TED, now.minusDays(50), "Salário"));

        // Mês 6 - últimos 30 dias
        transactions.add(createTransaction(accounts.get(0), accounts.get(2), new BigDecimal("850.00"),
            TransactionType.PIX, now.minusDays(25), "Aluguel"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(0), new BigDecimal("3500.00"),
            TransactionType.TED, now.minusDays(20), "Salário"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(4), new BigDecimal("200.00"),
            TransactionType.PIX, now.minusDays(15), "Ajuda financeira"));
        transactions.add(createTransaction(accounts.get(3), accounts.get(0), new BigDecimal("1500.00"),
            TransactionType.TED, now.minusDays(10), "Pagamento consultoria"));

        // Transações do Carlos (conta média - 3 meses de histórico)
        // Mês 1 - há 90 dias
        transactions.add(createTransaction(accounts.get(2), accounts.get(2), new BigDecimal("3000.00"),
            TransactionType.PIX, now.minusDays(90), "Depósito inicial"));
        transactions.add(createTransaction(accounts.get(3), accounts.get(3), new BigDecimal("20000.00"),
            TransactionType.TED, now.minusDays(90), "Capital inicial empresa"));
        transactions.add(createTransaction(accounts.get(2), accounts.get(0), new BigDecimal("500.00"),
            TransactionType.PIX, now.minusDays(85), "Pagamento freelance"));

        // Mês 2 - há 60 dias
        transactions.add(createTransaction(accounts.get(3), accounts.get(2), new BigDecimal("5000.00"),
            TransactionType.TED, now.minusDays(60), "Pró-labore"));
        transactions.add(createTransaction(accounts.get(2), accounts.get(4), new BigDecimal("600.00"),
            TransactionType.PIX, now.minusDays(55), "Pagamento serviço"));
        transactions.add(createTransaction(accounts.get(3), accounts.get(0), new BigDecimal("1200.00"),
            TransactionType.TED, now.minusDays(50), "Pagamento serviço"));

        // Mês 3 - últimos 30 dias
        transactions.add(createTransaction(accounts.get(3), accounts.get(2), new BigDecimal("5000.00"),
            TransactionType.TED, now.minusDays(30), "Pró-labore"));
        transactions.add(createTransaction(accounts.get(2), accounts.get(4), new BigDecimal("450.00"),
            TransactionType.PIX, now.minusDays(20), "Compras"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(2), new BigDecimal("500.00"),
            TransactionType.PIX, now.minusDays(15), "Pagamento projeto"));
        transactions.add(createTransaction(accounts.get(3), accounts.get(0), new BigDecimal("1500.00"),
            TransactionType.TED, now.minusDays(10), "Pagamento consultoria"));

        // Transações da Ana (conta nova - 1 mês de histórico)
        // Mês 1 - últimos 30 dias
        transactions.add(createTransaction(accounts.get(4), accounts.get(4), new BigDecimal("2000.00"),
            TransactionType.PIX, now.minusDays(30), "Depósito inicial"));
        transactions.add(createTransaction(accounts.get(5), accounts.get(5), new BigDecimal("5000.00"),
            TransactionType.TED, now.minusDays(30), "Transferência da conta antiga"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(4), new BigDecimal("300.00"),
            TransactionType.PIX, now.minusDays(25), "Presente aniversário"));
        transactions.add(createTransaction(accounts.get(4), accounts.get(2), new BigDecimal("600.00"),
            TransactionType.PIX, now.minusDays(20), "Pagamento serviço"));
        transactions.add(createTransaction(accounts.get(0), accounts.get(4), new BigDecimal("200.00"),
            TransactionType.PIX, now.minusDays(15), "Ajuda financeira"));
        transactions.add(createTransaction(accounts.get(4), accounts.get(4), new BigDecimal("1500.00"),
            TransactionType.PIX, now.minusDays(10), "Salário"));
        transactions.add(createTransaction(accounts.get(2), accounts.get(4), new BigDecimal("450.00"),
            TransactionType.PIX, now.minusDays(5), "Compras"));
        transactions.add(createTransaction(accounts.get(4), accounts.get(5), new BigDecimal("500.00"),
            TransactionType.PIX, now.minusDays(3), "Poupança"));

        return transactions;
    }

    private Transaction createTransaction(Account origin, Account destination, BigDecimal amount,
                                        TransactionType type, LocalDateTime date, String description) {
        Transaction transaction = new Transaction();
        transaction.setOriginAccount(origin);
        transaction.setDestinationAccount(destination);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDate(date);
        transaction.setStatus("Success");
        return transaction;
    }

    private List<BankStatement> createBankStatements(List<Account> accounts) {
        List<BankStatement> statements = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Extratos da Maria (6 meses)
        for (int i = 5; i >= 0; i--) {
            LocalDateTime startDate = now.minusMonths(i + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = now.minusMonths(i).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

            BankStatement stmt1 = createStatement(accounts.get(0), startDate, endDate,
                StatementType.MENSAL, i == 0 ? StatementStatus.VISUALIZADO : StatementStatus.ARQUIVADO);
            statements.add(stmt1);

            if (i <= 3) { // Poupança tem menos extratos
                BankStatement stmt2 = createStatement(accounts.get(1), startDate, endDate,
                    StatementType.MENSAL, StatementStatus.ARQUIVADO);
                statements.add(stmt2);
            }
        }

        // Extratos do Carlos (3 meses)
        for (int i = 2; i >= 0; i--) {
            LocalDateTime startDate = now.minusMonths(i + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endDate = now.minusMonths(i).withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

            BankStatement stmt1 = createStatement(accounts.get(2), startDate, endDate,
                StatementType.MENSAL, i == 0 ? StatementStatus.GERADO : StatementStatus.ARQUIVADO);
            statements.add(stmt1);

            BankStatement stmt2 = createStatement(accounts.get(3), startDate, endDate,
                StatementType.MENSAL, StatementStatus.ARQUIVADO);
            statements.add(stmt2);
        }

        // Extratos da Ana (1 mês)
        LocalDateTime startDate = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = now.withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

        BankStatement stmt1 = createStatement(accounts.get(4), startDate, endDate,
            StatementType.MENSAL, StatementStatus.VISUALIZADO);
        statements.add(stmt1);

        BankStatement stmt2 = createStatement(accounts.get(5), startDate, endDate,
            StatementType.MENSAL, StatementStatus.GERADO);
        statements.add(stmt2);

        // Alguns extratos personalizados recentes
        BankStatement personalizado1 = createStatement(accounts.get(0),
            now.minusDays(15), now, StatementType.PERSONALIZADO, StatementStatus.VISUALIZADO);
        statements.add(personalizado1);

        BankStatement personalizado2 = createStatement(accounts.get(2),
            now.minusDays(7), now, StatementType.PERSONALIZADO, StatementStatus.GERADO);
        statements.add(personalizado2);

        return statements;
    }

    private BankStatement createStatement(Account account, LocalDateTime startDate, LocalDateTime endDate,
                                         StatementType type, StatementStatus status) {
        BankStatement statement = new BankStatement();
        statement.setAccount(account);
        statement.setStartDate(startDate);
        statement.setEndDate(endDate);
        statement.setType(type);
        statement.setStatus(status);
        statement.setGeneratedAt(endDate.plusDays(1)); // Gerado no dia seguinte ao fim do período
        return statement;
    }

    private List<Beneficiary> createBeneficiaries(List<Account> accounts) {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Beneficiários da Maria
        Beneficiary benef1 = new Beneficiary();
        benef1.setOwnerAccount(accounts.get(0)); // Conta corrente Maria
        benef1.setName("Loja de Eletrônicos XYZ");
        benef1.setCpfCnpj("12.345.678/0001-90");
        benef1.setBankCode("001");
        benef1.setAgency("1234");
        benef1.setAccountNumber("56789-0");
        benef1.setAccountType(AccountType.EMPRESARIAL);
        benef1.setCreatedAt(now.minusDays(120));
        benef1.setUpdatedAt(now.minusDays(120));
        beneficiaries.add(benef1);

        Beneficiary benef2 = new Beneficiary();
        benef2.setOwnerAccount(accounts.get(0));
        benef2.setName("João Pedro Alves");
        benef2.setCpfCnpj("123.456.789-01");
        benef2.setBankCode("237");
        benef2.setAgency("0001");
        benef2.setAccountNumber("98765-4");
        benef2.setAccountType(AccountType.CORRENTE);
        benef2.setCreatedAt(now.minusDays(90));
        benef2.setUpdatedAt(now.minusDays(90));
        beneficiaries.add(benef2);

        // Beneficiários do Carlos
        Beneficiary benef3 = new Beneficiary();
        benef3.setOwnerAccount(accounts.get(2)); // Conta corrente Carlos
        benef3.setName("Fornecedor ABC Ltda");
        benef3.setCpfCnpj("98.765.432/0001-11");
        benef3.setBankCode("341");
        benef3.setAgency("5678");
        benef3.setAccountNumber("12345-6");
        benef3.setAccountType(AccountType.EMPRESARIAL);
        benef3.setCreatedAt(now.minusDays(60));
        benef3.setUpdatedAt(now.minusDays(60));
        beneficiaries.add(benef3);

        Beneficiary benef4 = new Beneficiary();
        benef4.setOwnerAccount(accounts.get(2));
        benef4.setName("Maria Silva");
        benef4.setCpfCnpj("111.222.333-44");
        benef4.setBankCode("001");
        benef4.setAgency("0001");
        benef4.setAccountNumber("1001001");
        benef4.setAccountType(AccountType.CORRENTE);
        benef4.setCreatedAt(now.minusDays(50));
        benef4.setUpdatedAt(now.minusDays(50));
        beneficiaries.add(benef4);

        // Beneficiários da Ana
        Beneficiary benef5 = new Beneficiary();
        benef5.setOwnerAccount(accounts.get(4)); // Conta corrente Ana
        benef5.setName("Loja de Roupas Fashion");
        benef5.setCpfCnpj("11.222.333/0001-44");
        benef5.setBankCode("104");
        benef5.setAgency("3456");
        benef5.setAccountNumber("78901-2");
        benef5.setAccountType(AccountType.EMPRESARIAL);
        benef5.setCreatedAt(now.minusDays(25));
        benef5.setUpdatedAt(now.minusDays(25));
        beneficiaries.add(benef5);

        return beneficiaries;
    }

    private List<Card> createCards(List<Account> accounts) {
        List<Card> cards = new ArrayList<>();

        // Cartões da Maria
        Card card1 = new Card();
        card1.setNumber("5412 7534 8901 2345");
        card1.setHolderName("MARIA SILVA");
        card1.setExpirationDate("12/2027");
        card1.setCvv("123");
        card1.setAccount(accounts.get(0));
        cards.add(card1);

        Card card2 = new Card();
        card2.setNumber("4111 1111 1111 1111");
        card2.setHolderName("MARIA SILVA");
        card2.setExpirationDate("06/2028");
        card2.setCvv("456");
        card2.setAccount(accounts.get(0));
        cards.add(card2);

        // Cartões do Carlos
        Card card3 = new Card();
        card3.setNumber("5500 0000 0000 0004");
        card3.setHolderName("CARLOS SANTOS");
        card3.setExpirationDate("03/2027");
        card3.setCvv("789");
        card3.setAccount(accounts.get(2));
        cards.add(card3);

        Card card4 = new Card();
        card4.setNumber("3782 822463 10005");
        card4.setHolderName("CARLOS SANTOS");
        card4.setExpirationDate("09/2028");
        card4.setCvv("321");
        card4.setAccount(accounts.get(3)); // Conta empresarial
        cards.add(card4);

        // Cartões da Ana
        Card card5 = new Card();
        card5.setNumber("4532 1488 0343 6467");
        card5.setHolderName("ANA COSTA");
        card5.setExpirationDate("11/2026");
        card5.setCvv("654");
        card5.setAccount(accounts.get(4));
        cards.add(card5);

        return cards;
    }

    private List<Loan> createLoans(List<Account> accounts) {
        List<Loan> loans = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Empréstimos da Maria
        Loan loan1 = new Loan();
        loan1.setAccount(accounts.get(0));
        loan1.setAmount(new BigDecimal("10000.00"));
        loan1.setInterestRate(new BigDecimal("2.5"));
        loan1.setInstallments(12);
        loan1.setPaidInstallments(8);
        loan1.setInstallmentValue(new BigDecimal("896.67"));
        loan1.setStatus(LoanStatus.APPROVED);
        loan1.setCreatedAt(now.minusDays(240));
        loan1.setUpdatedAt(now.minusDays(5));
        loans.add(loan1);

        Loan loan2 = new Loan();
        loan2.setAccount(accounts.get(0));
        loan2.setAmount(new BigDecimal("5000.00"));
        loan2.setInterestRate(new BigDecimal("1.9"));
        loan2.setInstallments(6);
        loan2.setPaidInstallments(6);
        loan2.setInstallmentValue(new BigDecimal("871.67"));
        loan2.setStatus(LoanStatus.PAID);
        loan2.setCreatedAt(now.minusDays(180));
        loan2.setUpdatedAt(now.minusDays(10));
        loans.add(loan2);

        // Empréstimos do Carlos
        Loan loan3 = new Loan();
        loan3.setAccount(accounts.get(3)); // Conta empresarial
        loan3.setAmount(new BigDecimal("50000.00"));
        loan3.setInterestRate(new BigDecimal("3.2"));
        loan3.setInstallments(24);
        loan3.setPaidInstallments(10);
        loan3.setInstallmentValue(new BigDecimal("2416.67"));
        loan3.setStatus(LoanStatus.APPROVED);
        loan3.setCreatedAt(now.minusDays(300));
        loan3.setUpdatedAt(now.minusDays(3));
        loans.add(loan3);

        Loan loan4 = new Loan();
        loan4.setAccount(accounts.get(2));
        loan4.setAmount(new BigDecimal("3000.00"));
        loan4.setInterestRate(new BigDecimal("2.0"));
        loan4.setInstallments(10);
        loan4.setPaidInstallments(0);
        loan4.setInstallmentValue(new BigDecimal("315.00"));
        loan4.setStatus(LoanStatus.PENDING);
        loan4.setCreatedAt(now.minusDays(5));
        loan4.setUpdatedAt(now.minusDays(5));
        loans.add(loan4);

        // Empréstimo da Ana
        Loan loan5 = new Loan();
        loan5.setAccount(accounts.get(4));
        loan5.setAmount(new BigDecimal("2000.00"));
        loan5.setInterestRate(new BigDecimal("1.5"));
        loan5.setInstallments(8);
        loan5.setPaidInstallments(2);
        loan5.setInstallmentValue(new BigDecimal("261.25"));
        loan5.setStatus(LoanStatus.APPROVED);
        loan5.setCreatedAt(now.minusDays(60));
        loan5.setUpdatedAt(now.minusDays(2));
        loans.add(loan5);

        Loan loan6 = new Loan();
        loan6.setAccount(accounts.get(4));
        loan6.setAmount(new BigDecimal("1500.00"));
        loan6.setInterestRate(new BigDecimal("2.2"));
        loan6.setInstallments(6);
        loan6.setPaidInstallments(0);
        loan6.setInstallmentValue(new BigDecimal("0.00"));
        loan6.setStatus(LoanStatus.REJECTED);
        loan6.setCreatedAt(now.minusDays(10));
        loan6.setUpdatedAt(now.minusDays(9));
        loans.add(loan6);

        return loans;
    }

    private List<Notification> createNotifications(List<User> users, List<Account> accounts) {
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Notificações da Maria
        notifications.add(new Notification(users.get(0), "Bem-vindo ao Agiota Bank!", NotificationType.USER_CREATED));
        notifications.add(new Notification(users.get(0), "Sua conta corrente foi criada com sucesso!", NotificationType.ACCOUNT_CREATED));
        notifications.add(new Notification(users.get(0), "Sua conta poupança foi criada com sucesso!", NotificationType.ACCOUNT_CREATED));
        notifications.add(new Notification(users.get(0), "Chave PIX CPF cadastrada com sucesso!", NotificationType.PIX_KEY_CREATED));
        notifications.add(new Notification(users.get(0), "Transferência de R$ 500,00 enviada com sucesso!", NotificationType.TRANSACTION_SENT));
        notifications.add(new Notification(users.get(0), "Você recebeu R$ 1.500,00 de Carlos Santos", NotificationType.TRANSACTION_RECEIVED));
        notifications.add(new Notification(users.get(0), "Seu cartão de crédito foi criado!", NotificationType.CARD_CREATED));
        notifications.add(new Notification(users.get(0), "Empréstimo de R$ 10.000,00 aprovado!", NotificationType.ACCOUNT_UPDATED));

        // Marcar algumas como lidas
        notifications.get(0).setRead(true);
        notifications.get(1).setRead(true);
        notifications.get(2).setRead(true);
        notifications.get(3).setRead(true);

        // Notificações do Carlos
        notifications.add(new Notification(users.get(1), "Bem-vindo ao Agiota Bank!", NotificationType.USER_CREATED));
        notifications.add(new Notification(users.get(1), "Sua conta corrente foi criada com sucesso!", NotificationType.ACCOUNT_CREATED));
        notifications.add(new Notification(users.get(1), "Sua conta empresarial foi criada com sucesso!", NotificationType.ACCOUNT_CREATED));
        notifications.add(new Notification(users.get(1), "Chave PIX CPF cadastrada com sucesso!", NotificationType.PIX_KEY_CREATED));
        notifications.add(new Notification(users.get(1), "Você recebeu R$ 500,00 de Maria Silva", NotificationType.TRANSACTION_RECEIVED));
        notifications.add(new Notification(users.get(1), "Transferência de R$ 1.200,00 enviada com sucesso!", NotificationType.TRANSACTION_SENT));
        notifications.add(new Notification(users.get(1), "Empréstimo empresarial de R$ 50.000,00 aprovado!", NotificationType.ACCOUNT_UPDATED));
        notifications.add(new Notification(users.get(1), "Novo empréstimo de R$ 3.000,00 em análise", NotificationType.ACCOUNT_UPDATED));

        notifications.get(8).setRead(true);
        notifications.get(9).setRead(true);
        notifications.get(10).setRead(true);

        // Notificações da Ana
        notifications.add(new Notification(users.get(2), "Bem-vindo ao Agiota Bank!", NotificationType.USER_CREATED));
        notifications.add(new Notification(users.get(2), "Sua conta corrente foi criada com sucesso!", NotificationType.ACCOUNT_CREATED));
        notifications.add(new Notification(users.get(2), "Sua conta poupança foi criada com sucesso!", NotificationType.ACCOUNT_CREATED));
        notifications.add(new Notification(users.get(2), "Chave PIX CPF cadastrada com sucesso!", NotificationType.PIX_KEY_CREATED));
        notifications.add(new Notification(users.get(2), "Você recebeu R$ 300,00 de Maria Silva", NotificationType.TRANSACTION_RECEIVED));
        notifications.add(new Notification(users.get(2), "Você recebeu R$ 200,00 de Maria Silva", NotificationType.TRANSACTION_RECEIVED));
        notifications.add(new Notification(users.get(2), "Empréstimo de R$ 2.000,00 aprovado!", NotificationType.ACCOUNT_UPDATED));
        notifications.add(new Notification(users.get(2), "Empréstimo de R$ 1.500,00 foi rejeitado", NotificationType.TRANSACTION_FAILED));

        notifications.get(16).setRead(true);
        notifications.get(17).setRead(true);

        return notifications;
    }

    private List<SupportTicket> createSupportTickets(List<User> users) {
        List<SupportTicket> tickets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Tickets da Maria
        SupportTicket ticket1 = new SupportTicket();
        ticket1.setUser(users.get(0));
        ticket1.setTitle("Dúvida sobre taxas de transferência");
        ticket1.setDescription("Gostaria de saber quais são as taxas cobradas para transferências TED e DOC.");
        ticket1.setStatus(SupportTicketStatus.CLOSED);
        ticket1.setResponse("Olá Maria! As taxas são: TED R$ 10,00 e DOC R$ 8,00. Agradecemos o contato!");
        ticket1.setCreatedAt(now.minusDays(120));
        ticket1.setUpdatedAt(now.minusDays(119));
        tickets.add(ticket1);

        SupportTicket ticket2 = new SupportTicket();
        ticket2.setUser(users.get(0));
        ticket2.setTitle("Problema ao gerar extrato personalizado");
        ticket2.setDescription("Tentei gerar um extrato de 6 meses mas recebi erro. Podem verificar?");
        ticket2.setStatus(SupportTicketStatus.CLOSED);
        ticket2.setResponse("Olá! Identificamos o problema. O período máximo para extratos personalizados é de 12 meses. Já corrigimos para você. Obrigado!");
        ticket2.setCreatedAt(now.minusDays(45));
        ticket2.setUpdatedAt(now.minusDays(44));
        tickets.add(ticket2);

        // Tickets do Carlos
        SupportTicket ticket3 = new SupportTicket();
        ticket3.setUser(users.get(1));
        ticket3.setTitle("Como solicitar aumento de limite do cartão?");
        ticket3.setDescription("Preciso de um limite maior no meu cartão empresarial. Como proceder?");
        ticket3.setStatus(SupportTicketStatus.IN_PROGRESS);
        ticket3.setResponse("Olá Carlos! Estamos analisando sua solicitação. Em breve entraremos em contato.");
        ticket3.setCreatedAt(now.minusDays(15));
        ticket3.setUpdatedAt(now.minusDays(14));
        tickets.add(ticket3);

        SupportTicket ticket4 = new SupportTicket();
        ticket4.setUser(users.get(1));
        ticket4.setTitle("Dúvida sobre empréstimo empresarial");
        ticket4.setDescription("Quais são as condições para empréstimo empresarial acima de R$ 100.000?");
        ticket4.setStatus(SupportTicketStatus.CLOSED);
        ticket4.setResponse("Olá! Para valores acima de R$ 100.000, as taxas variam entre 2.5% e 4% ao mês, com prazo de até 36 meses. Entre em contato com nosso gerente empresarial.");
        ticket4.setCreatedAt(now.minusDays(80));
        ticket4.setUpdatedAt(now.minusDays(78));
        tickets.add(ticket4);

        // Tickets da Ana
        SupportTicket ticket5 = new SupportTicket();
        ticket5.setUser(users.get(2));
        ticket5.setTitle("Como cadastrar chave PIX aleatória?");
        ticket5.setDescription("Não consigo cadastrar uma chave aleatória. O sistema pede CPF, email ou telefone.");
        ticket5.setStatus(SupportTicketStatus.OPEN);
        ticket5.setCreatedAt(now.minusDays(3));
        ticket5.setUpdatedAt(now.minusDays(3));
        tickets.add(ticket5);

        SupportTicket ticket6 = new SupportTicket();
        ticket6.setUser(users.get(2));
        ticket6.setTitle("Não recebi notificação de transação");
        ticket6.setDescription("Fiz uma transferência mas não recebi a notificação no app.");
        ticket6.setStatus(SupportTicketStatus.IN_PROGRESS);
        ticket6.setResponse("Olá Ana! Estamos verificando o problema com as notificações. Agradecemos a paciência.");
        ticket6.setCreatedAt(now.minusDays(8));
        ticket6.setUpdatedAt(now.minusDays(7));
        tickets.add(ticket6);

        return tickets;
    }

    private List<AuthorizedDevice> createAuthorizedDevices(List<User> users) {
        List<AuthorizedDevice> devices = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Dispositivos da Maria
        AuthorizedDevice device1 = new AuthorizedDevice();
        device1.setDeviceName("iPhone 13 Pro");
        device1.setDeviceType(DeviceType.MOBILE);
        device1.setIpAddress("192.168.1.100");
        device1.setLastLoginDate(now.minusHours(2));
        device1.setAuthorized(true);
        device1.setUser(users.get(0));
        devices.add(device1);

        AuthorizedDevice device2 = new AuthorizedDevice();
        device2.setDeviceName("MacBook Pro");
        device2.setDeviceType(DeviceType.DESKTOP);
        device2.setIpAddress("192.168.1.101");
        device2.setLastLoginDate(now.minusDays(1));
        device2.setAuthorized(true);
        device2.setUser(users.get(0));
        devices.add(device2);

        AuthorizedDevice device3 = new AuthorizedDevice();
        device3.setDeviceName("iPad Air");
        device3.setDeviceType(DeviceType.TABLET);
        device3.setIpAddress("192.168.1.102");
        device3.setLastLoginDate(now.minusDays(5));
        device3.setAuthorized(true);
        device3.setUser(users.get(0));
        devices.add(device3);

        // Dispositivos do Carlos
        AuthorizedDevice device4 = new AuthorizedDevice();
        device4.setDeviceName("Samsung Galaxy S23");
        device4.setDeviceType(DeviceType.MOBILE);
        device4.setIpAddress("192.168.1.200");
        device4.setLastLoginDate(now.minusHours(5));
        device4.setAuthorized(true);
        device4.setUser(users.get(1));
        devices.add(device4);

        AuthorizedDevice device5 = new AuthorizedDevice();
        device5.setDeviceName("Dell XPS 15");
        device5.setDeviceType(DeviceType.DESKTOP);
        device5.setIpAddress("192.168.1.201");
        device5.setLastLoginDate(now.minusDays(2));
        device5.setAuthorized(true);
        device5.setUser(users.get(1));
        devices.add(device5);

        AuthorizedDevice device6 = new AuthorizedDevice();
        device6.setDeviceName("Chrome Browser - Windows");
        device6.setDeviceType(DeviceType.WEB);
        device6.setIpAddress("201.45.78.90");
        device6.setLastLoginDate(now.minusDays(10));
        device6.setAuthorized(false); // Dispositivo desautorizado por segurança
        device6.setUser(users.get(1));
        devices.add(device6);

        // Dispositivos da Ana
        AuthorizedDevice device7 = new AuthorizedDevice();
        device7.setDeviceName("Xiaomi Redmi Note 12");
        device7.setDeviceType(DeviceType.MOBILE);
        device7.setIpAddress("192.168.1.300");
        device7.setLastLoginDate(now.minusMinutes(30));
        device7.setAuthorized(true);
        device7.setUser(users.get(2));
        devices.add(device7);

        AuthorizedDevice device8 = new AuthorizedDevice();
        device8.setDeviceName("Firefox Browser - Linux");
        device8.setDeviceType(DeviceType.WEB);
        device8.setIpAddress("192.168.1.301");
        device8.setLastLoginDate(now.minusDays(3));
        device8.setAuthorized(true);
        device8.setUser(users.get(2));
        devices.add(device8);

        return devices;
    }
}