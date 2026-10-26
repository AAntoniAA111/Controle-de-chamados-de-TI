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