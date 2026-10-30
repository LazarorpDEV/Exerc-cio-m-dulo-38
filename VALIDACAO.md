# Validação da entrega

Verificação realizada em 30/10/2026.

## Compilação e testes automatizados

- Java 17 e Maven 3.9.9.
- Comando: `mvn -Pservidor clean verify`.
- Resultado: **BUILD SUCCESS**.
- **23 testes executados, zero falhas, zero erros, zero testes ignorados.**
- WAR gerado e servidor Payara Micro baixado pelo perfil Maven.

## Integração com o banco real

Aplicação implantada no Payara Micro 6.2024.12, conectada a uma instância PostgreSQL 16.4 criada para a verificação.

Os testes enviaram requisições HTTP reais ao formulário JSF, preservando sessão, estado da página e os campos enviados. Os resultados foram consultados diretamente no PostgreSQL.

| Verificação | Resultado |
| --- | --- |
| Implantação do WAR e acesso à página JSF | Aprovado |
| Criação automática da tabela pelo JPA | Aprovado |
| Cadastro de produto pela página | Aprovado |
| Preço `19,90` persistido como `19.90` | Aprovado |
| Código repetido com diferença de maiúsculas | Rejeitado corretamente |
| Preço zero | Rejeitado corretamente |
| Estoque negativo | Rejeitado corretamente |
| Edição pela página | Aprovado |
| Preço `25,50` e estoque `7` confirmados no banco | Aprovado |
| Nova requisição mantém o registro persistido | Aprovado |
| Exclusão pela página | Aprovado |
| Exclusão confirmada com SELECT no banco | Aprovado |

Os testes de serviço usam um repositório simulado. O teste integrado acima usou o PostgreSQL, sem substituição por banco em memória.

## Limites da verificação

A opção Docker e o script PowerShell foram fornecidos para execução local e não foram executados neste ambiente. A integração foi verificada por HTTP; não houve inspeção visual em navegador. A publicação no GitHub e o envio à plataforma EBAC ainda devem ser feitos pelo aluno, seguindo o README.
