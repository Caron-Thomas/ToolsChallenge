## 💳 Serviço de Pagamentos API

Uma API RESTful para processamento de transações de pagamento, estorno e consultas, seguindo um fluxo de autorização externo simplificado.

---

### 🚀 Tecnologias e Pré-requisitos

O projeto foi construído utilizando as seguintes tecnologias e frameworks:

* **Java 21**: Versão utilizada no desenvolvimento, usar esta ou superior.
* **Spring Boot 3.5.6**: Framework essencial para a criação e configuração da API.
* **Maven**: Ferramenta de automação de construção e gerenciamento de dependências.

---

### 🛠️ Instruções de Execução

Para iniciar e testar o projeto localmente, siga os passos abaixo utilizando o **Maven**:

1.  **Compilar o Projeto:**
    Abra o terminal na raiz do projeto (onde está o arquivo `pom.xml`) e execute o comando para compilar e gerar o pacote JAR:
    ```bash
    mvn clean install
    ```

2.  **Executar a API:**
    Após a compilação, o arquivo JAR (neste exemplo: `toolschallenge-0.0.1-SNAPSHOT.jar`) estará na pasta `target/`. Execute-o com o comando Java.
    ```bash
    java -jar target/toolschallenge-0.0.1-SNAPSHOT.jar
    ```

A API estará rodando e pronta para receber requisições na porta **`8090`**.

3.  **Acesso ao H2 Console (Banco de Dados em Memória):**
    Enquanto a API estiver em execução, você pode acessar o console do banco de dados para visualizar ou gerenciar as transações:

    * **URL de Acesso:**
        ```
        http://localhost:8090/h2-console
        ```
    * **Credenciais de Conexão:**

| Campo | Valor                        |
| :--- |:-----------------------------|
| **JDBC URL** | `jdbc:h2:mem:toolschallenge` |
| **User Name** | `toolschallenge`             |
| **Password** |                              |
---

## ✨ Funcionalidades (Endpoints)

A API oferece as seguintes operações:

| Operação | Método HTTP | Endpoint | Descrição |
| :--- | :--- | :--- | :--- |
| **Pagamento** | `POST` | `http://localhost:8090/api/v1/pagamentos` | Cria uma nova transação de pagamento. **(Requer Request Body)** |
| **Estorno** | `POST` | `http://localhost:8090/api/v1/pagamentos/{id}/estorno` | Solicita o estorno de uma transação existente, usando o ID. |
| **Consulta (Por ID)** | `GET` | `http://localhost:8090/api/v1/pagamentos/{id}` | Retorna os detalhes de uma transação específica pelo seu ID. |
| **Consulta (Todos)** | `GET` | `http://localhost:8090/api/v1/pagamentos?page=0&size=20&sort=estabelecimento,desc` | Retorna a lista de todas as transações de forma **paginada** e com **ordenação**. |

### Detalhes da Consulta (Todos)

A consulta paginada permite ordenação pelos seguintes campos, que estão na entidade **`Transacao`** ou **`Descricao`**:

* **Campos de `Descricao`:** `valor`, `dataHora`, `estabelecimento`
* **Campos de `Transacao`:** `id`, `cartao`

O exemplo de URL mostra a ordenação por `estabelecimento` de forma descendente (`desc`).

---

## 📐 Design e Premissas

O projeto foi construído com as seguintes premissas para simular um ambiente real:

1.  **IDs de Transação e Timestamps:** Assumiu-se que o `transacaoId` e o `dataCriado` são gerados e validados por um serviço externo antes de chegar a esta API.
2.  **Autorização Externa:** O processo de autorização é simulado por uma classe simples (`Serviço Externo (Autorização)`), que retorna `AUTORIZADO` ou `NEGADO`.

### Fluxo de Processamento de Pagamento

O diagrama de sequência abaixo ilustra o fluxo de criação de uma transação, incluindo a comunicação com o serviço de autorização e o tratamento de casos como transação duplicada ou erro de comunicação.

```mermaid
sequenceDiagram
    participant A as API
    participant C as criarTransacao Controller
    participant S as cria transação Service
    participant E as Serviço Externo (Autorização)

    A->>C: /transacao (POST)
    activate C
    C->>S: criaTransacao(Transacao dados)
    activate S
    S->>S: Verifica se Transação Existe (ID)

    alt Transação Já Existe
        S-->>C: Exceção: Transação Já Existe
    else Transação Não Existe
        S->>E: Solicita Autorização
        activate E

        alt Erro de Comunicação
            E-->>S: Erro de Comunicação
            S-->>C: Exceção: Erro de Comunicação
        else Autorização Recebida
            E-->>S: Status: Autorizado/Não Autorizado
            deactivate E

            S->>S: Gera NSU
            S-->>C: Resposta Transação (com NSU)
        end
    end
    deactivate S
    C-->>A: Resposta da API (200/4xx/5xx)
    deactivate C

classDiagram
    direction LR

    class Transacao {
        - Long transacaoId
        - String cartao
        - LocalDateTime dataCriado
        - LocalDateTime dataAtualizado
        - Descricao descricao
        - TipoDePagamento tipoDePagamento
    }

    class Descricao {
        - BigDecimal valor
        - LocalDateTime dataHora
        - String estabelecimento
        - Long nsu
        - Long codigoAutorizacao
        - StatusDoPagamento statusDoPagamento
    }

    class TransacaoService {
        + List<Transacao> buscaTodas()
        + Transacao buscaPorId(Long id)
        + Transacao criaTransacao(Transacao)
        + Transacao criaTransacaoEstorno(Long id)
    }

    class TipoDePagamento {
        <<enum com.toolschallenge.enuns>>
        AVISTA
        PARCELADO_LOJA
        PARCELADO_EMISSOR
    }

    class StatusDoPagamento {
        <<enum com.toolschallenge.enuns>>
        AUTORIZADO
        NEGADO
        CANCELADO
    }

    Transacao "1" *-- "1" Descricao : tem
    Transacao "1" *-- "1" TipoDePagamento : tem
    Descricao "1" *-- "1" StatusDoPagamento : tem
    TransacaoService ..> Transacao : usa