# 🛠️ Sistema de Controle de Chamados de TI (TechServ)

> Solução desenvolvida com base no desafio prático de Desenvolvimento de Sistemas no formato SAEP (Sistema de Avaliação da Educação Profissional).

O **Sistema de Controle de Chamados de TI** foi projetado para resolver o problema de gestão da empresa **TechServ**, substituindo anotações e mensagens informais por um registro centralizado, estruturado e persistente de chamados de suporte técnico.

---

## 🚀 Tecnologias Utilizadas

- **Linguagem de Programação:** Java (JDK 17+)
- **Banco de Dados:** MySQL (via JDBC)
- **Gerenciador de Dependências:** Maven
- **IDE Recomendada:** IntelliJ IDEA / VS Code / Eclipse

---

## 📋 Funcionalidades e Regras de Negócio

### Funcionalidades
1. **Cadastrar Chamado:** Registra solicitante, descrição e prioridade.
2. **Listar Chamados:** Exibe todos os registros com ID, dados do solicitante, prioridade, status e data de abertura.
3. **Consultar Chamado por ID:** Busca rápida de um chamado específico.
4. **Atualizar Status:** Altera a situação do chamado respeitando as regras de transição.
5. **Filtrar Chamados:** Filtra por **Prioridade** (*Baixa, Média, Alta*) ou por **Status** (*Aberto, Em atendimento, Concluído*).
6. **Resumo por Status:** Apresenta a quantidade total de chamados categorizada por status.

### Regras de Negócio
- **ID:** Gerado automaticamente pelo banco de dados (`AUTO_INCREMENT`).
- **Solicitante:** Obrigatório, com pelo menos **3 caracteres válidos** (sem contar espaços das extremidades).
- **Descrição:** Obrigatória, com pelo menos **10 caracteres válidos** (sem contar espaços das extremidades).
- **Status Inicial:** Todo chamado inicia automaticamente como **Aberto**.
- **Fluxo de Transição Permitido:** `Aberto` ➔ `Em atendimento` ➔ `Concluído`. *(Saltar diretamente de 'Aberto' para 'Concluído' é bloqueado).*
- **Segurança SQL:** Todas as consultas e alterações utilizam declarações parametrizadas (`PreparedStatement`) para evitar vulnerabilidades de SQL Injection.

---

## 🗄️ Estrutura do Banco de Dados

### Script DDL (`schema.sql`)

```sql
CREATE DATABASE IF NOT EXISTS controle_chamados;
USE controle_chamados;

CREATE TABLE IF NOT EXISTS chamados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    solicitante VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    prioridade ENUM('Baixa', 'Média', 'Alta') NOT NULL,
    status ENUM('Aberto', 'Em atendimento', 'Concluído') NOT NULL DEFAULT 'Aberto',
    data_abertura DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔧 Como Configurar e Executar

### Pré-requisitos
- **Java JDK 17** ou superior instalado.
- Servidor **MySQL** em execução (local ou remoto).

### 1. Clonar o Repositório
```bash
git clone https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
cd SEU_REPOSITORIO
```

### 2. Configurar o Banco de Dados
Execute o script SQL fornecido acima no MySQL Workbench ou no seu terminal MySQL para criar o banco de dados `controle_chamados` e a tabela `chamados`.

### 3. Ajustar as Credenciais de Conexão
No arquivo `Main.java`, ajuste as variáveis `URL`, `USER` e `PASS` com as informações do seu servidor MySQL:

```java
private static final String URL = "jdbc:mysql://localhost:3306/controle_chamados";
private static final String USER = "seu_usuario";
private static final String PASS = "sua_senha";
```

### 4. Executar a Aplicação
No IntelliJ IDEA:
1. Sincronize as dependências do Maven (clique no ícone de reload do Maven no arquivo `pom.xml`).
2. Abra a classe `Main.java`.
3. Clique em **Run** (`Shift + F10`).

---

## 🧪 Plano de Testes e Validação

O sistema passou por todos os testes funcionais especificados na avaliação SAEP:

| Teste | Procedimento | Resultado Esperado | Status |
| :---: | :--- | :--- | :---: |
| **T01** | Cadastrar chamado com dados válidos | Registro criado com ID e status 'Aberto' | **Aprovado** |
| **T02** | Solicitante apenas com espaços | Cadastro recusado com orientação | **Aprovado** |
| **T03** | Descrição com < 10 caracteres | Cadastro recusado com orientação | **Aprovado** |
| **T04** | Prioridade não permitida | Operação recusada | **Aprovado** |
| **T05** | Consultar ID inexistente | Mensagem clara sem encerrar o app | **Aprovado** |
| **T06** | Transição: Aberto ➔ Em atendimento | Status atualizado | **Aprovado** |
| **T07** | Transição: Aberto ➔ Concluído (direta) | Alteração recusada | **Aprovado** |
| **T08** | Transição: Em atendimento ➔ Concluído | Status atualizado | **Aprovado** |
| **T09** | Reabrir a aplicação | Dados preservados no MySQL | **Aprovado** |
| **T10** | Filtro e resumo por status | Resumo preciso (ex: 2 abertos, 1 em atendimento, 1 concluído) | **Aprovado** |

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais e de avaliação prática baseada nos critérios da SAEP. Sinta-se à vontade para utilizar e melhorar o código!
