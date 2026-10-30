-- Executar UMA VEZ no pgAdmin, conectado ao banco postgres, fora de transação.
CREATE DATABASE modulo38;

-- Depois conecte-se ao banco modulo38 e execute a aplicação.
-- O JPA cria a tabela produto automaticamente na primeira consulta.
-- Após cadastrar pela tela, abra uma consulta no banco modulo38:
-- SELECT id, codigo, nome, preco, estoque, versao FROM produto ORDER BY id;
