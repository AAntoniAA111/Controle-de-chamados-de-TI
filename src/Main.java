
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/controle_chamados";
    private static final String USER = "root";  // Altere para seu usuário do MySQL
    private static final String PASS = "1234.5678";  // Altere para sua senha do MySQL

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Scanner scanner = new Scanner(System.in)) {

            String opcao = "";
            while (!opcao.equals("0")) {
                System.out.println("\n--- SISTEMA DE CHAMADOS DE TI ---");
                System.out.println("1. Cadastrar Chamado");
                System.out.println("2. Listar Todos os Chamados");
                System.out.println("3. Consultar Chamado por ID");
                System.out.println("4. Atualizar Status do Chamado");
                System.out.println("5. Filtrar Chamados");
                System.out.println("6. Resumo por Status");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                opcao = scanner.nextLine().trim();

                switch (opcao) {
                    case "1" -> cadastrarChamado(conn, scanner);
                    case "2" -> listarChamados(conn);
                    case "3" -> consultarChamado(conn, scanner);
                    case "4" -> atualizarStatus(conn, scanner);
                    case "5" -> filtrarChamados(conn, scanner);
                    case "6" -> resumoPorStatus(conn);
                    case "0" -> System.out.println("Encerrando o sistema...");
                    default -> System.out.println("Opção inválida!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro de Conexão MySQL: " + e.getMessage());
        }
    }

    private static void cadastrarChamado(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("\n--- Novo Chamado ---");
        System.out.print("Solicitante: ");
        String solicitante = scanner.nextLine().trim();
        if (solicitante.length() < 3) {
            System.out.println("Erro: O nome do solicitante deve ter pelo menos 3 caracteres válidos.");
            return;
        }

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();
        if (descricao.length() < 10) {
            System.out.println("Erro: A descrição deve ter pelo menos 10 caracteres válidos.");
            return;
        }

        System.out.print("Prioridade (Baixa, Média, Alta): ");
        String prioridadeInput = scanner.nextLine().trim();
        String prioridade = prioridadeInput;

        if (prioridadeInput.equalsIgnoreCase("media")) prioridade = "Média";
        if (prioridadeInput.equalsIgnoreCase("baixa")) prioridade = "Baixa";
        if (prioridadeInput.equalsIgnoreCase("alta")) prioridade = "Alta";

        if (!prioridade.equals("Baixa") && !prioridade.equals("Média") && !prioridade.equals("Alta")) {
            System.out.println("Erro: Prioridade inválida. Escolha entre Baixa, Média ou Alta.");
            return;
        }

        String sql = "INSERT INTO chamados (solicitante, descricao, prioridade) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, solicitante);
            stmt.setString(2, descricao);
            stmt.setString(3, prioridade);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    System.out.println("✓ Chamado registrado com sucesso! ID Gerado: " + rs.getInt(1) + " | Status: Aberto");
                }
            }
        }
    }

    private static void listarChamados(Connection conn) throws SQLException {
        String sql = "SELECT * FROM chamados";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            boolean encontrou = false;
            System.out.println("\n--- Lista de Chamados ---");
            while (rs.next()) {
                encontrou = true;
                System.out.printf("[ID: %d] | Solicitante: %s | Prioridade: %s | Status: %s | Data: %s%n",
                        rs.getInt("id"),
                        rs.getString("solicitante"),
                        rs.getString("prioridade"),
                        rs.getString("status"),
                        rs.getTimestamp("data_abertura"));
                System.out.println("     Descrição: " + rs.getString("descricao"));
            }
            if (!encontrou) {
                System.out.println("Nenhum chamado encontrado.");
            }
        }
    }

    private static void consultarChamado(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Informe o ID do chamado: ");
        String input = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        String sql = "SELECT * FROM chamados WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n--- Chamado #" + rs.getInt("id") + " ---");
                    System.out.println("Solicitante: " + rs.getString("solicitante"));
                    System.out.println("Descrição: " + rs.getString("descricao"));
                    System.out.println("Prioridade: " + rs.getString("prioridade"));
                    System.out.println("Status: " + rs.getString("status"));
                    System.out.println("Data: " + rs.getTimestamp("data_abertura"));
                } else {
                    System.out.println("Mensagem: Chamado com ID " + id + " não encontrado.");
                }
            }
        }
    }

    private static void atualizarStatus(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Informe o ID do chamado: ");
        String input = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        String selectSql = "SELECT status FROM chamados WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Mensagem: Chamado com ID " + id + " não encontrado.");
                    return;
                }

                String statusAtual = rs.getString("status");
                System.out.println("Status atual do chamado #" + id + ": " + statusAtual);

                if (statusAtual.equals("Concluído")) {
                    System.out.println("Este chamado já está concluído e não pode mais alterar de status.");
                    return;
                }

                System.out.println("Status possíveis: \"Em atendimento\" ou \"Concluído\"");
                System.out.print("Novo status: ");
                String novoStatus = scanner.nextLine().trim();

                if (statusAtual.equals("Aberto")) {
                    if (!novoStatus.equalsIgnoreCase("em atendimento")) {
                        System.out.println("Erro de Transição: Um chamado 'Aberto' só pode ser alterado para 'Em atendimento'.");
                        return;
                    }
                    novoStatus = "Em atendimento";
                } else if (statusAtual.equals("Em atendimento")) {
                    if (!novoStatus.equalsIgnoreCase("concluído") && !novoStatus.equalsIgnoreCase("concluido")) {
                        System.out.println("Erro de Transição: Um chamado 'Em atendimento' só pode ser alterado para 'Concluído'.");
                        return;
                    }
                    novoStatus = "Concluído";
                }

                String updateSql = "UPDATE chamados SET status = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, novoStatus);
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                    System.out.println("✓ Status alterado com sucesso para \"" + novoStatus + "\"!");
                }
            }
        }
    }

    private static void filtrarChamados(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("1. Filtrar por Prioridade (Baixa, Média, Alta)");
        System.out.println("2. Filtrar por Status (Aberto, Em atendimento, Concluído)");
        System.out.print("Escolha: ");
        String opcao = scanner.nextLine().trim();

        if (opcao.equals("1")) {
            System.out.print("Informe a prioridade: ");
            String p = scanner.nextLine().trim();
            String sql = "SELECT * FROM chamados WHERE prioridade = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, p);
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("\n--- Resultados para Prioridade: " + p + " ---");
                    while (rs.next()) {
                        System.out.printf("[ID: %d] %s - %s (%s)%n", rs.getInt("id"), rs.getString("solicitante"), rs.getString("descricao"), rs.getString("status"));
                    }
                }
            }
        } else if (opcao.equals("2")) {
            System.out.print("Informe o status: ");
            String s = scanner.nextLine().trim();
            String sql = "SELECT * FROM chamados WHERE status = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, s);
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("\n--- Resultados para Status: " + s + " ---");
                    while (rs.next()) {
                        System.out.printf("[ID: %d] %s - Prioridade: %s - %s%n", rs.getInt("id"), rs.getString("solicitante"), rs.getString("prioridade"), rs.getString("descricao"));
                    }
                }
            }
        }
    }

    private static void resumoPorStatus(Connection conn) throws SQLException {
        String sql = "SELECT status, COUNT(*) as total FROM chamados GROUP BY status";
        int abertos = 0, emAtendimento = 0, concluidos = 0;

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String status = rs.getString("status");
                int total = rs.getInt("total");
                if ("Aberto".equals(status)) abertos = total;
                if ("Em atendimento".equals(status)) emAtendimento = total;
                if ("Concluído".equals(status)) concluidos = total;
            }
        }

        System.out.println("\n--- Resumo de Chamados por Status ---");
        System.out.println("• Abertos: " + abertos);
        System.out.println("• Em atendimento: " + emAtendimento);
        System.out.println("• Concluídos: " + concluidos);
    }
}