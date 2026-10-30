# Módulo 38 — Aplicação JSF com JPA e PostgreSQL

Autor: Lázaro Rodrigues Pereira.

Aplicação web para cadastrar, listar, editar e excluir produtos. A página envia os dados ao controller, o serviço valida as regras e o repositório persiste a entidade no PostgreSQL usando JPA. Tema escolhido: produtos de uma cozinha/restaurante.

## Requisitos do exercício

| Requisito | Implementação |
| --- | --- |
| Página JSF | `src/main/webapp/index.xhtml` |
| Controller | `controller/ProdutoController.java` |
| Serviço e regras | `service/ProdutoService.java` |
| Repositório JPA | `repository/ProdutoJpaRepository.java` |
| Entidade mapeada | `domain/Produto.java` |
| PostgreSQL | `META-INF/persistence.xml` e variáveis de conexão |
| Versionamento | Instruções de Git ao final deste arquivo |

## Tecnologias

Java 17, Maven, Jakarta EE 10, Jakarta Faces 4 (JSF), CDI, JPA com EclipseLink, Bean Validation, PostgreSQL e Payara Micro 6.2024.12. O servidor fornece as implementações Jakarta EE; o driver PostgreSQL está no WAR. As versões do exercício estão fixadas no `pom.xml`.

É uma aplicação Jakarta (`jakarta.*`). Use o Payara Micro baixado pelo próprio projeto. Não execute o WAR em um Tomcat simples ou em servidor antigo com `javax.*`.

## Opção 1 — Windows, Java/Maven e PostgreSQL local

1. Instale/tenha Java 17, Maven e PostgreSQL. Confira:

```powershell
java -version
mvn -version
```

2. No pgAdmin, conectado ao banco `postgres`, execute **uma vez**, fora de uma transação:

```sql
CREATE DATABASE modulo38;
```

O arquivo `banco.sql` contém esse comando. O banco precisa existir; o JPA cria a tabela `produto` ao acessar a aplicação pela primeira vez.

3. Abra a pasta extraída `Atividade_Modulo38` no VS Code e abra um terminal PowerShell nessa pasta. Informe os dados do seu PostgreSQL (troque a senha pela sua):

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/modulo38"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "SUA_SENHA_DO_POSTGRES"
mvn -Pservidor clean verify
```

A primeira execução baixa as dependências e o servidor, que tem aproximadamente 100 MB. Aguarde a mensagem `BUILD SUCCESS`.

4. No **mesmo terminal**, execute:

```powershell
java -jar target/servidor/payara-micro.jar --deploy target/modulo38.war --port 8080 --noCluster --disablephonehome
```

Aguarde a conclusão da implantação. Deixe esse terminal aberto e acesse:

**http://localhost:8080/modulo38/**

Nas próximas execuções você também pode usar `powershell -ExecutionPolicy Bypass -File .\iniciar.ps1`, após definir as variáveis de conexão no terminal. O script compila, testa e inicia a aplicação. Para encerrar, pressione `Ctrl+C`.

No Linux/macOS, configure `DB_URL`, `DB_USER` e `DB_PASSWORD` com `export` e execute `sh iniciar.sh`.

Sem variáveis, os valores locais padrão são `jdbc:postgresql://localhost:5432/modulo38`, usuário `postgres` e senha `postgres`. Ajuste as variáveis para o seu ambiente. Não coloque senhas pessoais no Git.

## Opção 2 — Docker Desktop

Com Docker Desktop iniciado e o terminal na pasta do projeto:

```powershell
docker compose up --build
```

O Compose cria o PostgreSQL, aguarda o banco ficar disponível, compila/testa o projeto e inicia o Payara. Você não precisa instalar Java, Maven ou PostgreSQL no computador nessa opção.

Acesse **http://localhost:8080/modulo38/** após a implantação. A porta do PostgreSQL do contêiner não é publicada, evitando conflito com seu PostgreSQL local. O banco usa um volume persistente. A senha `postgres` é somente para este exercício local.

Para executar uma consulta no banco do Docker:

```powershell
docker compose exec banco psql -U postgres -d modulo38 -c "SELECT id, codigo, nome, preco, estoque FROM produto ORDER BY id;"
```

Para parar preservando os dados: `docker compose down`. Evite a opção `-v` se quiser manter os cadastros.

## Como validar a atividade

1. Cadastre código `MASSA-01`, nome `Massa fresca`, preço `19,90` e estoque `10`.
2. Confira a mensagem de sucesso e o produto na listagem.
3. Cadastre outro produto, por exemplo `MOLHO-01`, `Molho de tomate`, `12,50`, estoque `5`.
4. Tente cadastrar `massa-01`: o serviço converte o código para maiúsculas e rejeita duplicidade.
5. Tente preço zero e estoque negativo: o cadastro deve ser rejeitado.
6. Clique em **Editar**, altere preço ou estoque e salve.
7. Atualize a página ou reinicie a aplicação: os registros continuam no banco.
8. Confira pelo pgAdmin, conectado especificamente ao banco `modulo38`:

```sql
SELECT id, codigo, nome, preco, estoque, versao
FROM produto
ORDER BY id;
```

9. Use **Excluir**, confirme e confira a remoção na tela e no banco.

Para registrar a execução, tire seus próprios prints da tela com produtos, da validação e do SELECT no seu PostgreSQL. O enunciado pede versionamento; os prints ajudam a demonstrar que você executou o sistema.

## Regras de negócio e persistência

- Código obrigatório, no máximo 30 caracteres, sem espaços nas extremidades e em maiúsculas.
- Código único, verificado pelo serviço e também por uma restrição no banco.
- Nome obrigatório, no máximo 100 caracteres.
- Preço a partir de R$ 0,01, até 10 dígitos inteiros e 2 casas decimais.
- Estoque inteiro obrigatório, maior ou igual a zero.
- Transação com commit/rollback e fechamento do EntityManager por operação.
- `@Version` evita sobrescrever silenciosamente uma edição concorrente.
- As credenciais vêm de variáveis de ambiente.

O JPA usa transações locais (`RESOURCE_LOCAL`) gerenciadas no repositório. O serviço cuida das regras, e o controller das ações e mensagens. A fábrica de EntityManager é única na aplicação; o EntityManager não é compartilhado entre requisições.

A criação automática é adequada para o exercício. Alterações futuras mais complexas do esquema exigem migrações. O sistema é um exercício local sem autenticação.

## Testes e compilação

```powershell
mvn clean verify
```

Os testes JUnit verificam o serviço usando um repositório simulado, sem depender do PostgreSQL. Para baixar também o servidor, execute `mvn -Pservidor clean verify`.

O build gera `target/modulo38.war`. O CRUD com JSF e a gravação no PostgreSQL devem ser verificados com a aplicação iniciada conforme o roteiro acima. Veja também `VALIDACAO.md` para o que foi conferido na preparação desta entrega.

## Enviar ao GitHub

Crie no seu GitHub um repositório vazio chamado **modulo38**, sem adicionar README ou .gitignore pelo site. Depois, no terminal aberto na pasta deste projeto:

```powershell
git init
git add .
git commit -m "Implementa cadastro JSF com servico, JPA e PostgreSQL"
git branch -M main
git remote add origin https://github.com/LazarorpDEV/modulo38.git
git push -u origin main
```

Se já existir um `origin`, confira com `git remote -v` e ajuste-o com `git remote set-url origin https://github.com/LazarorpDEV/modulo38.git` se necessário.

Confira no site se `pom.xml`, `src` e `README.md` aparecem. A pasta `target` não deve ser enviada. Depois envie na plataforma da EBAC o link do seu repositório: `https://github.com/LazarorpDEV/modulo38`.

O projeto em ZIP não publica automaticamente no GitHub: o push precisa ser executado na sua conta.

## Solução de problemas

- **Connection refused:** confira o serviço PostgreSQL, host e porta em `DB_URL`.
- **Password authentication failed:** use a senha correta em `DB_PASSWORD`, no mesmo terminal em que inicia o Java.
- **Database modulo38 does not exist:** execute o `CREATE DATABASE` no pgAdmin antes de iniciar.
- **Porta 8080 ocupada:** altere `--port 8080` para `--port 8081` e use a nova porta no navegador. Com Docker, altere apenas o lado esquerdo do mapeamento para `127.0.0.1:8081:8080`.
- **Não encontro a tabela:** selecione o banco `modulo38`, abra a aplicação e atualize `Schemas > public > Tables` no pgAdmin.
- **Erro ao salvar após edição em outra janela:** atualize a lista e edite novamente.
- **Página 404:** aguarde a implantação e utilize o caminho `/modulo38/` completo.
