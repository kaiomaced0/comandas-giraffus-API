# Atividade — Gestão de documentos/arquivos com MinIO (object storage)

> **Status:** 🟡 PLANEJADA (atividades definidas; aguardando priorização para implementar).
> **Origem:** solicitação do Kaio (2026-06-10) — "temos controle de documentos? veja viabilidade e crie atividades p/ MinIO, garantindo segurança sempre".
> **Escopo:** API Quarkus (broker de storage) + comandas-web (upload/preview). Multi-tenant.

## 1. Estado atual (diagnóstico)

**Não existe controle de documentos/arquivos na API hoje.** Levantado no código:

- **Nenhum endpoint de upload.** Todos os `@Path` consomem `application/json` (nenhum `multipart/form-data`).
- **Imagens são apenas URLs externas.** `Produto.linkimage` é `@NotBlank String` (`dto/ProdutoDTO.java`) — o cliente envia uma URL; o seeder aponta para um **Firebase de terceiro** hardcoded (`startup/DataInitializer.java`). Não há bytes nem armazenamento próprio.
- **`quarkus-resteasy-multipart` está no `pom.xml` mas NÃO é usado** por nenhum resource (capacidade presente, não fiada).
- **`DocumentoFiscal`** guarda só texto JSON (`payloadEmissao`/`payloadRetorno`) — **não** é arquivo/PDF.
- **Sem** MinIO, S3, AWS SDK ou qualquer object storage.

**Onde arquivos/documentos são (ou serão) necessários:**
1. **Imagem de produto** — hoje URL externa; deveria ser upload próprio. (TASKS web: "Upload de imagem (placeholder)".)
2. **Logo da empresa** — TASKS web Fase 5 "Upload de logo (placeholder)".
3. **PDF de documento fiscal** (cupom/NFC-e emulada) — TASKS Onda I: "Imprimir PDF" pendente.
4. **Exports de relatórios** (CSV/PDF) — Onda K (hoje CSV é gerado no cliente).
5. Futuro: comprovantes de pagamento, anexos diversos.

## 2. Viabilidade

**Alta.** Justificativa:
- **Quarkus 3.31 + MinIO** é bem suportado: extensão `quarkus-minio` (Quarkiverse) **ou** `quarkus-amazon-s3` (MinIO é S3-compatível) **ou** o SDK oficial `io.minio:minio`. Recomendo a extensão `quarkus-minio` (cliente injetável + dev services) ou o SDK MinIO se preferir menos mágica.
- **Infra local trivial:** a máquina já roda Docker (containers ollama/etc.); subir um container MinIO em dev é simples (compose ou Quarkus Dev Services).
- **Multi-tenant:** resolvido por convenção de chave de objeto + verificação de posse na API (ver §3).
- **Não bloqueia nada:** é aditivo; `linkimage` continua funcionando (migração gradual).

## 3. Arquitetura proposta (SEGURANÇA EM PRIMEIRO LUGAR)

**Princípio central: a API é o ÚNICO broker do storage.** O cliente (web/app) **nunca** recebe credenciais do MinIO nem fala direto com ele para escrever. Toda escrita passa pela API (que valida e autoriza); leitura é feita por **URL pré-assinada de TTL curto** emitida pela API após checar posse.

- **Bucket privado único** (ex.: `comandas`) com **acesso público DESLIGADO** (deny public). Isolamento multi-tenant por **prefixo de chave**: `empresa-{empresaId}/{tipo}/{uuid}-{nomeSanitizado}`.
  - Alternativa (isolamento físico mais forte): **bucket por empresa**. Mais gestão; adotar só se exigência de compliance pedir. Padrão recomendado: bucket único + prefixo + posse checada na API.
- **Upload:** `POST /documentos` (multipart) **pela API** — valida tipo/tamanho/extensão, gera a chave (UUID, nunca o nome cru), grava no MinIO, persiste metadados. (Evitar presigned PUT direto do cliente nesta fase — a API validando é mais seguro.)
- **Download:** `GET /documentos/{id}/url` → a API checa posse (empresa do JWT == empresa do objeto) e devolve **presigned GET com TTL curto (ex.: 5 min)**. Sem URLs públicas/perenes.
- **Credenciais MinIO** (access key/secret) **só no servidor**, via env/secrets manager — nunca no código nem no cliente. Liga com o [`HARDENING.md`](../../docs/HARDENING.md) (Onda N).
- **Entidade de metadados** `Documento`: `id`, `empresa`, `tipo` (enum: PRODUTO_IMG, EMPRESA_LOGO, FISCAL_PDF, RELATORIO, OUTRO), `objectKey`, `contentType`, `tamanhoBytes`, `nomeOriginal`, `checksumSha256`, `criadoPor` (Usuario), `dataInclusao`, `ativo`. As entidades de domínio referenciam `Documento` (ex.: `Produto.imagemDocumentoId`), mantendo `linkimage` como fallback durante a migração.

## 4. ✅ Checklist de SEGURANÇA (obrigatório — "garantir segurança sempre")

- [ ] **Bucket privado**: política nega acesso anônimo/público; nada servido por URL perene.
- [ ] **API como único broker**: cliente nunca recebe credenciais; escrita só via API autenticada.
- [ ] **Isolamento multi-tenant**: chave sempre prefixada por `empresa-{id}`; **todo** GET/DELETE valida `empresaDoJWT == empresaDoObjeto` (negar 404/403 caso contrário). Teste automatizado de não-vazamento cross-tenant.
- [ ] **Presigned URLs de TTL curto** (≤ 5 min) para download; nunca presigned de escrita exposto ao cliente nesta fase.
- [ ] **Validação de upload**: allowlist de content-type + extensão (ex.: image/png, image/jpeg, application/pdf), **limite de tamanho** (ex.: 5 MB img, 10 MB pdf), sniffing do conteúdo real (magic bytes, não confiar no header), **sanitização** do nome (sem path traversal; chave sempre via UUID).
- [ ] **Credenciais em env/secrets** (nunca commitadas); rotação documentada. Em dev, chaves locais ok (mesmo critério do `HARDENING.md`).
- [ ] **Criptografia**: SSE (server-side encryption) no MinIO em repouso + **TLS** em trânsito (HTTPS no MinIO em produção).
- [ ] **Autorização por papel**: quem pode subir/excluir (ex.: ADMIN/MASTER para logo; ADMIN/CAIXA para fiscal) via `@RolesAllowed`.
- [ ] **Auditoria**: log de quem subiu/baixou/excluiu qual objeto (ligar com auditoria futura).
- [ ] **Antimalware** (avaliar): varredura (ex.: ClamAV) para uploads de origem não confiável — pelo menos registrar como risco se não implementar.
- [ ] **Lifecycle/limpeza**: remover objetos órfãos (documento inativado → expurgo agendado); evitar acúmulo.
- [ ] **Rate limit** nos endpoints de upload (anti-abuso) — alinhar com o hardening de `/auth`.

## 5. Atividades (ATIs) — ordem sugerida

### ATI-1 — Infra MinIO (dev) 🟡
Subir MinIO via Docker (compose ou Quarkus Dev Services), bucket privado `comandas`, credenciais em env (`MINIO_URL`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET`). **Pronto quando:** MinIO acessível em dev, bucket criado, console acessível, sem acesso público.

### ATI-2 — Camada de storage + metadados na API 🟡
Adicionar extensão (`quarkus-minio` ou SDK), `StorageService` (abstração: `upload(empresaId, tipo, bytes, contentType, nome) → objectKey`; `presignedGet(objectKey, ttl)`; `delete(objectKey)`), entidade `Documento` + repository. Chave sempre prefixada por empresa. **Pronto quando:** upload/download programático funciona com isolamento por prefixo; metadados persistidos.

### ATI-3 — Endpoints seguros 🟡
`POST /documentos` (multipart; valida tipo/tamanho/extensão/sniffing; `@RolesAllowed`); `GET /documentos` (lista da empresa); `GET /documentos/{id}/url` (presigned TTL curto + checagem de posse); `DELETE /documentos/{id}` (soft + expurgo). **Pronto quando:** nenhum acesso cross-tenant; cliente sem credenciais; limites aplicados. Testes de isolamento e de validação.

### ATI-4 — Casos de uso de domínio 🟡
Imagem de produto (campo `Produto.imagemDocumentoId`, mantendo `linkimage` como fallback), logo da empresa, PDF de documento fiscal (gerar + guardar no MinIO; ligar ao "Imprimir PDF" da Onda I). **Pronto quando:** produto/empresa usam objeto do MinIO via presigned URL.

### ATI-5 — Web (upload + preview) 🟡
UI de upload em produto e configurações da empresa (drag-drop/input file), preview via presigned URL, troca do placeholder atual. **Pronto quando:** upload e preview funcionando ponta a ponta.

### ATI-6 — Hardening de produção 🟡
SSE + TLS no MinIO, política de bucket deny-public revisada, rotação de credenciais, auditoria, lifecycle de órfãos, (avaliar) antimalware, rate limit de upload. **Pronto quando:** checklist de segurança (§4) 100% fechado antes do deploy público.

### ATI-7 — Testes 🟡
Unit do `StorageService` (com MinIO de teste/Testcontainers ou fake), validação de limites/tipos, **teste de não-vazamento multi-tenant** (empresa A não acessa objeto de B), TTL de presigned. **Pronto quando:** suíte verde cobrindo segurança e isolamento.

## 6. Decisões a confirmar com o Kaio
- Extensão: **`quarkus-minio`** (recomendado) vs `quarkus-amazon-s3` vs SDK puro.
- Isolamento: **bucket único + prefixo** (recomendado) vs bucket por empresa.
- Antimalware (ClamAV) entra no MVP ou fica como risco aceito em dev?
- Onde rodar o MinIO em produção (mesmo host? serviço gerenciado?).

> Nada implementado ainda — este documento define as atividades. Ao priorizar, começar por ATI-1 → ATI-2 → ATI-3 (base segura) antes dos casos de uso.
