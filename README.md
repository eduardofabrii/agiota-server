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
- **Docker & Docker Compose**
- **Lombok**
- **MapStruct**
- **Maven**

---

# 🚀 Como Rodar o Projeto

Existem duas maneiras de executar a aplicação: utilizando Docker (recomendado) ou configurando o ambiente localmente.

## Opção 1: Rodando com Docker (Recomendado)

Este método é o mais simples, pois configura e conecta o banco de dados e a API automaticamente.

### Pré-requisitos
- [Git](https://git-scm.com)
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Passos
1.  **Clone o repositório:**
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
