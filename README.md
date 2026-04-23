# comandas-giraffus-API

API REST do sistema de gestão de comandas. É a **fonte única da verdade** do
ecossistema — consumida pelo app Flutter [`flutter-comandas`](../flutter-comandas)
(operação em mobile) e pela nova web de gestão
[`comandas-web`](../comandas-web).

Levantamento completo do ecossistema:
[`../docs/LEVANTAMENTO.md`](../docs/LEVANTAMENTO.md).

## Stack

- **Java 21** + **Quarkus 3.2** (Jakarta EE, não Spring Boot)
- **Hibernate ORM com Panache**
- **PostgreSQL** (principal) / **MariaDB** suportado
- **SmallRye JWT** (RSA) para autenticação
- **SmallRye OpenAPI** (Swagger UI)
- **PBKDF2WithHmacSHA512** para hashing de senhas (em migração para Argon2id)
- Docker: `Dockerfile.jvm`, `Dockerfile.native`, `Dockerfile.native-micro`

## Domínio

Sistema multi-tenant lógico (isolado por `empresa` do usuário logado).

Entidades principais: `Empresa`, `Usuario`, `Caixa`, `Comanda`, `Pedido`,
`ItemCompra`, `Produto`, `TipoProduto`, `Pagamento`, `EmpresaPagamento`.

Perfis de acesso: `MASTER`, `ADMIN`, `GARCOM`, `CAIXA`, `COZINHA`.

Formas de pagamento (enum atual): `CREDITO`, `DEBITO`, `PIX`, `AVISTA`.

## Recursos REST (visão geral)

| Recurso | Base path | Quem usa |
|---|---|---|
| Auth | `/auth` | público — login |
| Usuário logado | `/usuariologado` | qualquer autenticado |
| Usuário (admin do sistema) | `/usuario` | MASTER, ADMIN |
| Gerente (admin de empresa) | `/gerente` | ADMIN |
| Empresa | `/empresa` | MASTER |
| Fatura SaaS | `/empresapagamento` | MASTER |
| Comanda | `/comanda` | ADMIN, CAIXA, GARCOM, COZINHA |
| Pedido | `/pedido` | ADMIN, CAIXA, GARCOM |
| Item de compra | `/itemcompra` | ADMIN, CAIXA, GARCOM, MASTER |
| Produto | `/produto` | ADMIN, GARCOM, CAIXA, COZINHA |
| Tipo de produto | `/tipoproduto` | ADMIN, GARCOM, CAIXA, COZINHA |
| Caixa | `/caixa` | ADMIN, CAIXA |
| Pagamento | `/pagamento` | ADMIN, CAIXA |
| Sistema (público) | `/sistema/produtos` | público |

Lista completa de endpoints e DTOs no levantamento geral.

## Executando

Pré-requisitos: Java 21, Maven 3.9+, PostgreSQL rodando em `localhost:5432`.

```bash
# desenvolvimento com live reload
./mvnw compile quarkus:dev

# build JVM
./mvnw package

# build native
./mvnw package -Pnative

# container JVM
./mvnw package -Dquarkus.container-image.build=true
```

Após subir, acesse:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Health: `http://localhost:8080/q/health` (quando `smallrye-health` for incluído)

## Configuração

`src/main/resources/application.properties` controla conexão com o banco, chaves
RSA (`token/publicKey.pem`, `token/privateKey.pem`) e logging.

**⚠️ Antes de usar em produção:** externalizar credenciais via variáveis de
ambiente. Hoje a senha do banco está em texto plano no arquivo — ver seção
"Segurança" abaixo.

## Segurança — status atual

Conforme [`../docs/LEVANTAMENTO.md`](../docs/LEVANTAMENTO.md) §2.6, itens a
endereçar antes de produção:

- [ ] Externalizar `quarkus.datasource.password` (hoje hardcoded `123456`)
- [ ] Substituir salt hardcoded por salt por usuário + migrar hash para Argon2id
- [ ] Reduzir TTL do JWT (hoje 700 dias) e implementar refresh token rotativo
- [ ] Corrigir cálculo de gorjeta em `PagamentoServiceImpl` (sinal invertido)
- [ ] Remover `@PermitAll` de `GET /caixa/atual`
- [ ] Adicionar rate limiting em `POST /auth` e `POST /usuario`
- [ ] `ExceptionMapper` global com Problem Details (RFC 7807)
- [ ] Paginação e filtros nos `GET` coletivos
- [ ] CORS configurado explicitamente

## Lacunas funcionais conhecidas

- Integração real com gateways de pagamento (Abacate Pay, Asaas, TEFs)
- Relatórios agregados (vendas, fechamento detalhado, estoque)
- Auditoria global de mudanças
- WebSocket/SSE para KDS e caixa em tempo real
- Emissão de documento fiscal (NFC-e / SAT)
- Recuperação de senha (DTO existe, endpoint não)

Detalhamento e roadmap em
[`../docs/LEVANTAMENTO.md`](../docs/LEVANTAMENTO.md).

## Estrutura

```
src/main/java/.../
├── dominio/        # entidades JPA (Entity)
├── repository/     # PanacheRepository
├── service/        # interfaces e regras de negócio
├── resource/       # endpoints REST (JAX-RS)
├── dto/            # records de entrada/saída
└── security/       # JWT, hash, tokens

src/main/resources/
├── application.properties
└── token/          # chaves RSA (publicKey.pem, privateKey.pem)
```

## Relacionamento com os demais projetos

```
┌─────────────────────────┐
│  comandas-giraffus-API  │  ← este repositório
└────────────┬────────────┘
             │
      ┌──────┴──────┐
      ▼             ▼
 ┌──────────┐ ┌──────────────┐
 │ Flutter  │ │ comandas-web │
 │ (mobile) │ │ (gestão)     │
 └──────────┘ └──────────────┘
```
