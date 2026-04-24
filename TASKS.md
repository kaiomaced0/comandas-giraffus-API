# TASKS — comandas-giraffus-API

Lista de atividades derivada do [`../docs/LEVANTAMENTO.md`](../docs/LEVANTAMENTO.md)
e de [`../docs/ESCALABILIDADE.md`](../docs/ESCALABILIDADE.md). Agrupada por
fase do roadmap. Convenções:

- `[ ]` = a fazer · `[x]` = feito
- Tarefas marcadas com **`🧪`** exigem teste unitário do service junto com a
  implementação (regra geral: **toda service nova ou alterada vai com teste**).
- Prefixo **`BUG`** = correção de comportamento incorreto existente.
- Prefixo **`API`** = alteração de contrato externo.
- Prefixo **`DB`** = mudança de schema (gera migração).
- Prefixo **`SEC`** = item de segurança (pode ficar pendente em dev — ver §Fase pré-produção).

---

## Fase 0 — Correções críticas de domínio

Alvo: modelo de dados correto e bugs que afetam contabilidade. Base para tudo
que vem depois.

### Bugs e limpeza
- [x] **BUG** Corrigir cálculo de gorjeta em `PagamentoServiceImpl` — hoje é
      `preco - valorPagamento`, deve ser `valorPagamento - preco` **🧪**
- [x] Remover `main()` debug de `HashServiceImpl` que imprime hash em stdout
- [x] Padronizar filtro de soft delete: extrair helper ou usar `@Filter` do
      Hibernate em todos os repositories para sempre filtrar `ativo=true`
      (usado `@Where(clause = "ativo = true")` — Hibernate 6.2 em Quarkus 3.2
      ainda usa `@Where`; migrar para `@SQLRestriction` quando upgrade para
      Hibernate 6.3+).
- [x] `ExceptionMapper<Throwable>` global retornando Problem Details (RFC 7807)
- [x] `ExceptionMapper` específico para `NotFoundException`, `NotAuthorizedException`,
      `ConstraintViolationException`

### Refatoração: Caixa por usuário
- [x] **DB** Migração: adicionar `usuario_id` em `caixa` (via JPA
      `quarkus.hibernate-orm.database.generation=update`; sem Flyway nesta rodada)
- [x] **DB** Remover `empresa.caixa_atual_id` (não faz mais sentido)
- [x] **DB** Adicionar campos em `caixa`: `valor_abertura`, `valor_fechamento_esperado`,
      `valor_fechamento_informado`, `diferenca`, `hora_abertura`, `hora_fechamento`,
      `observacoes_fechamento`, `fechado_por_id`
- [x] Alterar `CaixaService` para operar no caixa do usuário logado **🧪**
- [x] `POST /caixa/abrir` exige `valorAbertura` e bloqueia se usuário já tem caixa aberto **🧪**
- [x] `PATCH /caixa/fechar` exige `valorFechamentoInformado` e gera `diferenca`;
      se diferença ≠ 0, exigir `observacoesFechamento` **🧪**
- [x] Endpoint `GET /caixa/meu` — retorna caixa aberto do usuário logado (substitui `/caixa/atual`)
- [x] Endpoint `GET /caixa/empresa` — admin vê todos os caixas abertos na empresa **🧪**
- [x] Endpoint `POST /caixa/{id}/fechar-forcado` (admin) — fecha caixa de outro
      usuário com justificativa e auditoria **🧪**
- [x] Remover `@PermitAll` de `GET /caixa/atual` (endpoint foi removido)

### Refatoração: Mesa como entidade
- [x] **DB** Criar tabela `mesa` (id, empresa_id, numero/identificador, capacidade, ativo)
- [x] **DB** Adicionar `mesa_id` (nullable) em `comanda`
- [ ] **DB** Backfill: tentar parsear `comanda.nome` como número e criar mesas
      correspondentes; comandas sem número viram delivery (`mesa_id=null`)
      — adiado: sem Flyway nesta rodada; será script manual na migração futura.
- [x] `MesaResource` com CRUD (`GET /mesa`, `POST /mesa`, `PATCH /mesa/{id}`, `PATCH /mesa/delete/{id}`) **🧪**
- [x] `GET /mesa/{id}/comandas` — lista comandas abertas naquela mesa (pode ser N)
- [x] Permitir **múltiplas comandas simultâneas na mesma mesa** — nenhuma
      constraint única em `(mesa_id, ativo)`
- [x] `ComandaDTO` passa a aceitar `mesaId` opcional

### Refatoração: Pagamentos múltiplos
- [x] **DB** Alterar `pagamento.comanda_id` de unique para index (permitir N)
- [x] **DB** Adicionar `modo` (enum SIMPLES|RATEADO), `caixa_id`, `valor_total`, `estornado` em `pagamento`
- [x] **DB** Criar tabela `pagamento_item` (pagamento_id, item_compra_id, quantidade, valor_abatido)
- [x] `PagamentoService.insert` valida:
      - modo SIMPLES: `soma(pagamentos.valorTotal) <= comanda.preco`
      - modo RATEADO: cada `PagamentoItem.quantidade` ≤ `ItemCompra.quantidade` restante **🧪**
- [x] `GET /comanda/{id}/pagamentos` — lista todos os pagamentos da comanda
- [x] `POST /comanda/{id}/pagamentos` — registra novo pagamento (payload com modo + itens opcionais)
- [x] `PATCH /pagamento/{id}/estornar` — marca estornado, regride `comanda.finalizada` se necessário **🧪**
- [x] Regra: comanda só finaliza quando `soma(pagamentos válidos) = comanda.preco`
- [x] Corrigir cálculo de gorjeta para trabalhar com N pagamentos **🧪**

### Refatoração: Cliente sob demanda (fiscal)
- [x] **DB** Criar tabela `cliente` (empresa_id, cpf, nome opcional, email opcional)
      com unique `(empresa_id, cpf)`
- [x] `POST /cliente` — cria ou retorna existente por CPF na empresa **🧪**
- [x] `GET /cliente/cpf/{cpf}` — busca para reutilizar

### Documento fiscal emulado
- [x] **DB** Criar tabela `documento_fiscal` (tipo NFCE|NFE, comanda_id, cliente_id?,
      numero, chave_acesso, status_emissao, emulado bool, payload_emissao, payload_retorno,
      emitido_em, usuario_emissao_id)
- [x] **DB** Criar tabela de junção `documento_fiscal_pagamento` (ManyToMany)
- [x] `FiscalService.emitirEmulado(comandaId, clienteId?)` — gera número fictício, marca como `EMITIDO`, `emulado=true` **🧪**
- [x] `POST /comanda/{id}/fiscal` — emissão sobre a comanda inteira
- [x] `POST /pagamento/{id}/fiscal` — emissão sobre um pagamento específico
- [x] `POST /fiscal/consolidado` — consolidar N pagamentos em um único documento
- [x] `GET /fiscal/{id}` — consulta
- [x] `POST /fiscal/{id}/cancelar`
- [x] Interface `FiscalProvider` com implementação `EmuladoFiscalProvider`
      (futuro: `ErpFiscalProvider`)

### Movimentações
- [x] **DB** Criar tabela `movimento_caixa` (tipo SANGRIA|SUPRIMENTO|TRANSFERENCIA,
      caixa_id, caixa_destino_id?, valor, motivo, usuario_id, data)
- [x] `POST /caixa/{id}/sangria`, `POST /caixa/{id}/suprimento` **🧪**
- [x] `POST /caixa/{id}/transferir/{destinoId}` **🧪**
- [x] Incluir movimentos no cálculo de `valor_fechamento_esperado` **🧪**
- [x] **DB** Criar tabela `movimento_estoque` (produto_id, tipo, quantidade, motivo, usuario_id, data)
- [x] Ao criar `Pedido`, gerar `MovimentoEstoque` tipo VENDA para cada `ItemCompra`
- [x] `adicionaEstoque` e `retiraEstoque` geram movimento com motivo **🧪**
- [x] `GET /produto/{id}/movimentacoes`

### Testes unitários — base
- [x] Configurar `quarkus-junit5`, `rest-assured`, H2 em memória para testes
      (Testcontainers/PostgreSQL fica para pré-produção — ver README em `src/test/`).
- [x] `AbstractServiceTest` — setup comum (usuário autenticado, empresa, dados seed)
- [x] Cobrir TODAS as services listadas acima com pelo menos os cenários:
      caminho feliz, validação de dados inválidos, regra de negócio.
      Caminho "permissão negada" via HTTP fica para Fase 1 (testes de resource).
- [ ] Meta de cobertura mínima: **70%** em services — medição formal via JaCoCo
      não configurada nesta rodada (35 testes unitários cobrindo 6 services
      críticos; instrumentação fica para follow-up).

---

## Fase 1 — Paridade com Flutter (suporte ao que o app precisa)

### Paginação
- [ ] **API** Introduzir `PagedResponse<T> { data, page, size, total }` para offset (web)
- [ ] **API** Introduzir `CursorResponse<T> { data, nextCursor }` para cursor (mobile)
- [ ] `GET /comanda`, `/pedido`, `/produto`, `/pagamento` suportam ambos os modos
      via query params (`?page=` ou `?cursor=`)
- [ ] Cursor = `{dataInclusao, id}` base64 — ordenação estável descendente

### Filtros
- [ ] `GET /comanda?mesaId=&finalizada=&from=&to=&atendenteId=`
- [ ] `GET /pedido?status=&comandaId=&from=&to=`
- [ ] `GET /produto?tipoProdutoId=&search=&emEstoque=`
- [ ] `GET /pagamento?caixaId=&formaPagamento=&from=&to=&usuarioId=`

### Status de pedido
- [ ] `PATCH /pedido/{id}/status` aceita payload `{ status: StatusPedido }` **🧪**
- [ ] Validar transição: `AGUARDANDO → PREPARANDO → PRONTO → ENTREGUE`
- [ ] Registrar histórico de transições em `pedido_status_historico` (opcional)

### Endpoints para o app que já estão cobertos pela refatoração
- [ ] `PATCH /caixa/fechar/{id}` com novo payload (valores informados)
- [ ] `POST /pagamento` no novo formato (modo + itens)

---

## Fase 2 — Apoio ao comandas-web

### CORS e autenticação
- [ ] Configurar CORS explícito com allowlist (`quarkus.http.cors.origins`)
- [ ] Habilitar OpenAPI com UI em `/q/swagger-ui` (já habilitado no pom; conferir)
- [ ] Documentar schemas via `@Schema` nos DTOs

### Endpoints específicos da gestão
- [ ] `GET /dashboard/kpis?from=&to=` — faturamento, ticket médio, top produtos, performance por garçom **🧪**
- [ ] `GET /dashboard/vendas-por-hora?data=` — heatmap
- [ ] `GET /caixa/{id}/fechamento-pdf` — gera PDF de fechamento
- [ ] `GET /comanda/{id}/pre-conta-pdf` — gera PDF da pré-conta

---

## Fase 3 — Gestão avançada

### Relatórios
- [ ] `GET /relatorios/vendas?from=&to=&groupBy=(produto|categoria|garcom|formaPagamento|hora|dia)` **🧪**
- [ ] `GET /relatorios/caixa?usuarioId=&from=&to=` — fechamentos
- [ ] `GET /relatorios/estoque?from=&to=` — movimentações
- [ ] `GET /relatorios/fiscal?from=&to=&status=` — documentos fiscais emitidos
- [ ] Exportação CSV em todos os relatórios via `Accept: text/csv`
- [ ] Exportação PDF em todos os relatórios via `Accept: application/pdf`

### Auditoria
- [ ] **DB** Tabela `auditoria` (usuario_id, entidade, entidade_id, acao, payload_antes, payload_depois, data)
- [ ] Interceptor CDI `@Audited` em services que modificam dados
- [ ] `GET /auditoria?entidade=&entidadeId=&usuarioId=&from=&to=`

### Taxa de serviço
- [ ] **DB** Alterar `comanda.taxa_servico` de `Boolean` para `BigDecimal` (percentual)
- [ ] **DB** Adicionar `comanda.taxa_servico_aplicada` (histórico — valor na hora do fechamento)
- [ ] Configuração padrão por empresa em `empresa.taxa_servico_padrao`

---

## Fase 4 — Pagamentos integrados

### Gateways
- [ ] **DB** Tabela `empresa_gateway_config` (empresa_id, tipo, api_key_cifrada, api_secret_cifrada, metadata JSONB, ambiente, ativo)
- [ ] Serviço de criptografia simétrica para `api_key`/`api_secret` (AES-GCM com chave mestra via env)
- [ ] `GET/POST/PUT /empresa/gateways` **🧪**
- [ ] `POST /empresa/gateways/{id}/testar` — ping no gateway **🧪**

### Transações
- [ ] **DB** Tabela `transacao_pagamento` (ver §5.2 do LEVANTAMENTO)
- [ ] **DB** Tabela `webhook_recebido`
- [ ] Interface `GatewayProvider` com implementações `AbacatePayProvider`, `AsaasProvider`
- [ ] `POST /pagamento/{id}/transacoes/iniciar` — cria transação pendente **🧪**
- [ ] `POST /pagamento/{id}/transacoes/{txId}/capturar`
- [ ] `POST /pagamento/{id}/transacoes/{txId}/estornar`
- [ ] Suporte a `Idempotency-Key` header em `POST /pagamento/*` **🧪**

### Webhooks
- [ ] `POST /webhooks/abacate` com validação de assinatura HMAC
- [ ] `POST /webhooks/asaas` com validação
- [ ] Persistir em `webhook_recebido`, enfileirar processamento (v1 síncrono; Fase 5 async)
- [ ] Idempotência: rejeitar webhook com mesmo `external_id` já processado **🧪**

---

## Fase 5 — Fiscal real e escala

- [ ] Integração real com ERP via `ErpFiscalProvider` (quando ERP for definido)
- [ ] Migração de registros emulados quando viável
- [ ] WebSocket/SSE para KDS (ver `../docs/ESCALABILIDADE.md` §9)
- [ ] Fila (RabbitMQ) para webhooks e emissão fiscal (§8)
- [ ] Redis para cache de catálogo (§6)
- [ ] Métricas Prometheus em `/q/metrics` (§12)
- [ ] Tracing OpenTelemetry
- [ ] Health checks `/q/health/live` e `/q/health/ready`

---

## Fase pré-produção — Hardening de segurança

> Blocante só antes do primeiro deploy público. Em desenvolvimento estes
> itens podem ficar pendentes — ver §2.6 do levantamento.

- [ ] **SEC** Externalizar `quarkus.datasource.password` via `${DB_PASSWORD}`
- [ ] **SEC** Migrar hash de senha para Argon2id (ou BCrypt work factor ≥ 12)
      com salt por usuário armazenado na tabela **🧪**
- [ ] **SEC** Reduzir TTL do JWT (700d → 15min access); implementar refresh token rotativo (7d)
- [ ] **SEC** Blacklist de JWT revogado (via Redis)
- [ ] **SEC** Rate limiting em `POST /auth` e `POST /usuario` (via `bucket4j-redis`)
- [ ] **SEC** Captcha + verificação de e-mail em `POST /usuario` ou remover `@PermitAll`
- [ ] **SEC** Endpoint `POST /auth/esqueci-senha` + token de reset por e-mail
- [ ] **SEC** Mensagem de erro de login genérica ("credenciais inválidas")
- [ ] **SEC** Headers de segurança globais (HSTS, X-Frame-Options, CSP, X-Content-Type-Options)
- [ ] **SEC** Validação de força de senha no registro
- [ ] **SEC** 2FA/MFA (TOTP) opcional para perfis ADMIN/MASTER
- [ ] **SEC** Revisar permissões por endpoint — garantir que nenhum `@PermitAll` resta sem justificativa
- [ ] **SEC** Prefixo `/api/v1/` em todos os resources (sem quebrar Flutter sem deprecation)

---

## Dívidas contínuas (não têm fase fixa)

- [ ] Escrever testes unitários para toda service **nova** — regra de projeto
- [ ] Manter OpenAPI anotado (`@Operation`, `@ApiResponse`) em endpoints novos
- [ ] Revisar `pom.xml` periodicamente para upgrades de segurança
- [ ] Monitorar `PagamentoRemovidoHistorico` — extender para auditoria genérica (Fase 3)
- [ ] Documentar cada migração Flyway/Liquibase com ticket de referência
