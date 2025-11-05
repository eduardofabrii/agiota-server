# 💰 Agiota Bank - Backend

Este é o backend do projeto **Agiota Bank**, uma API RESTful completa desenvolvida com **Spring Boot** e **Maven**.
O projeto agora inclui funcionalidades para transações via **PIX**, gerenciamento de chaves e um sistema de **notificações por e-mail**.

A aplicação utiliza **Spring MVC**, **Spring Data JPA**, **Spring Security** com **JWT**, **Lombok** e **MapStruct** para otimizar a produtividade e a organização do código.

---

## ✨ Principais Funcionalidades

* **Autenticação**: Sistema de login seguro com JSON Web Token (JWT).
* **Gerenciamento de Usuários**: CRUD completo para usuários e perfis (`USER`, `ADMIN`).
* **Contas Bancárias**: Criação e consulta de contas para os usuários.
* **Transações PIX**: Realização de transferências entre contas utilizando chaves PIX.
* **Chaves PIX**: Gerenciamento completo de chaves PIX (CPF, E-mail, Aleatória, etc.).
* **Notificações**: Envio de notificações e e-mails para os usuários sobre atividades na conta.
* **Documentação**: API documentada com Swagger (OpenAPI) para fácil exploração.

---

## ⚙️ Tecnologias Utilizadas
- **Java 17+**
- **Spring Boot**
- **Spring Web MVC**
- **Spring Data JPA**
- **Spring Security**
- **Spring Boot Mail Sender**
- **MySQL** (banco de dados principal)
- **Lombok**
- **MapStruct**
- **Maven**

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
- [Git](https://git-scm.com)
- [JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi) (ou use o wrapper `mvnw` incluído no projeto)
- [MySQL 8.0+](https://dev.mysql.com/downloads/mysql/)

### Passos

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/agiota-server.git
cd agiota-server
```

2. **Configure o MySQL:**
   - Instale o MySQL na sua máquina (se ainda não tiver)
   - Inicie o serviço MySQL
   - Crie o banco de dados (opcional, pois a aplicação cria automaticamente):
   ```sql
   CREATE DATABASE agiotabank;
   ```

3. **Configure as credenciais do banco:**
   - Abra o arquivo `src/main/resources/application.properties`
   - Altere a senha do MySQL se necessário (padrão: `senhadoagiotabank`):
   ```properties
   spring.datasource.password=SUA_SENHA_AQUI
   ```

4. **Compile o projeto:**
```bash
./mvnw clean install
```
   Ou no Windows:
```cmd
mvnw.cmd clean install
```

5. **Execute a aplicação:**
```bash
./mvnw spring-boot:run
```
   Ou no Windows:
```cmd
mvnw.cmd spring-boot:run
```

6. **Acesse a API:**
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 📚 Documentação da API

A documentação completa da API está disponível através do Swagger UI quando a aplicação estiver rodando:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Autenticação

A API utiliza JWT para autenticação. Para acessar endpoints protegidos:

1. Faça login através do endpoint `/auth/login`
2. Use o token JWT retornado no header `Authorization: Bearer {token}`

---

## 📝 Configurações Importantes

### Banco de Dados
- **URL**: `jdbc:mysql://localhost:3306/agiotabank`
- **Usuário padrão**: `root`
- **Senha padrão**: `senhadoagiotabank`

### JPA/Hibernate
- **DDL Auto**: `update` (cria e atualiza tabelas automaticamente)
- **Show SQL**: `true` (mostra queries SQL no console)

### Segurança
- **Token Secret**: Configurado em `application.properties`

---

## 🛠️ Desenvolvimento

### Rodando em modo de desenvolvimento
```bash
./mvnw spring-boot:run
```

### Compilando para produção
```bash
./mvnw clean package -DskipTests
java -jar target/agiota-server-*.jar
```

---

## 📧 Contato

Para dúvidas ou sugestões, entre em contato através do e-mail: agiotabankk@gmail.com
    ```bash
    git clone [https://github.com/eduardofabrii/agiota-server.git](https://github.com/eduardofabrii/agiota-server.git)
    cd agiota-server
    ```

2.  **Suba os contêineres:**
    Este comando irá construir a imagem da aplicação e iniciar a API junto com o banco de dados.
    ```bash
    docker-compose up -d --build
    ```

3.  **Acesse a aplicação:**
    -   **API:** `http://localhost:8080`
    -   **Documentação Swagger:** `http://localhost:8080/swagger-ui.html`

4.  **Para parar a aplicação:**
    ```bash
    docker-compose down
    ```

## Opção 2: Rodando Localmente (Sem Docker)

Este método requer que você tenha o Java, Maven e um servidor MySQL instalados e configurados na sua máquina.

### Pré-requisitos
-   Java 17 ou superior
-   Maven
-   MySQL Server

### Passos
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/eduardofabrii/agiota-server.git](https://github.com/eduardofabrii/agiota-server.git)
    cd agiota-server
    ```

2.  **Configure o Banco de Dados:**
    Crie um banco de dados no seu MySQL chamado `agiotabank`.

3.  **Configure as variáveis de ambiente:**
    No arquivo `src/main/resources/application.properties`, altere as seguintes linhas com suas credenciais do MySQL e do seu serviço de e-mail (ex: Gmail):
    ```properties
    # Configuração do Banco de Dados
    spring.datasource.url=jdbc:mysql://localhost:3306/agiotabank
    spring.datasource.username=SEU_USUARIO_MYSQL
    spring.datasource.password=SUA_SENHA_MYSQL

    # Configuração de E-mail (Exemplo para Gmail)
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=seu-email@gmail.com
    spring.mail.password=SUA_SENHA_DE_APP_AQUI 
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    ```
    > **Atenção:** Se você usa Gmail, é necessário gerar uma **"Senha de App"** na sua Conta Google para permitir que a aplicação envie e-mails. Não use sua senha principal.

4.  **Execute a aplicação:**
    Use o Maven Wrapper para iniciar o servidor.
    ```bash
    ./mvnw spring-boot:run
    ```
    Ou, se tiver o Maven instalado globalmente:
    ```bash
    mvn spring-boot:run
    ```
    A aplicação estará disponível em `http://localhost:8080`.

---

### Autores:

-   Lucas Stopinski da Silva 
-   Eduardo Henrique Fabri 
-   João Vitor Correa Oliveira
-   Marco Alija Ramos Agostini
-   Richard Mickaell Santos Nascimento
-   Rodrigo da Silva Alves
