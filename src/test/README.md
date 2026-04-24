# Test suite

- Testes de service usam H2 em memória (MODE=PostgreSQL) configurado via
  `src/test/resources/application.properties`.
- Dependência `quarkus-jdbc-h2` com scope `test` em `pom.xml`.
- `AbstractServiceTest` provê seed comum (empresa + usuários + produto + mesa
  + tipoProduto) e faz login com Admin automaticamente.
- Rodar: `./mvnw test`.
