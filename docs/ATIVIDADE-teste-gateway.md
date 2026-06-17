# Atividade ABERTA — Teste live de gateways de pagamento

> **Status:** 🟡 EM ABERTO (aguardando credenciais de dev do Kaio).
> **Origem:** Onda M (ver [`../../docs/WAVES.md`](../../docs/WAVES.md)).
> **Criada em:** 2026-06-10.

## Objetivo

Validar a integração **real** (sandbox) dos gateways de pagamento, rodando os
testes **localmente**. A estrutura de configuração já está pronta (CRUD +
`/testar` emulado); falta a comunicação real com os provedores.

## O que JÁ está pronto (não precisa refazer)

- **API:** `EmpresaGatewayConfig` (entity + repository + service multi-tenant),
  `/empresa/gateways` (CRUD) e `POST /empresa/gateways/{id}/testar` — hoje é um
  **stub emulado** (valida campos, não chama o provedor). Segredos mascarados na
  resposta. 9 testes unitários.
- **WEB:** feature `features/gateways` (rota `/gateways`): CRUD Abacate Pay/Asaas,
  ambiente sandbox/produção, apiKey/secret, botão "Testar conexão".

## O que FALTA (esta atividade)

1. **Cliente HTTP do provedor** na API (Abacate Pay e/ou Asaas) usando
   `quarkus-rest-client`.
2. **`POST /empresa/gateways/{id}/testar`** deixar de ser stub: fazer um ping
   real no sandbox (ex.: consultar saldo/credenciais) e retornar `emulado=false`.
3. **`TransacaoPagamento`** (entity) + criar cobrança PIX (Abacate) / cartão/PIX
   (Asaas), persistindo `externalId`, `status`, `qrCodePix`, etc.
4. **Webhook** (`POST /webhooks/{gateway}`) com validação de assinatura para
   confirmar pagamento assíncrono.
5. **WEB:** UI de pagamento com QR Code PIX / checkout (na tela da comanda).

## ⛏️ PREENCHER — credenciais/requisições de dev por gateway

> Kaio: cole abaixo os dados de **sandbox** de cada gateway que quiser testar.
> NÃO comitar segredos de produção; sandbox de dev é aceitável aqui.

### Abacate Pay (PIX)
- [ ] Base URL (sandbox):
- [ ] API key (sandbox):
- [ ] Doc da API / endpoints a usar:
- [ ] Observações:

### Asaas (PIX / cartão / boleto)
- [ ] Base URL (sandbox):
- [ ] API key (sandbox):
- [ ] Doc da API / endpoints a usar:
- [ ] Observações:

### (Outro gateway, se necessário)
- [ ] Nome / Base URL / API key / doc:

## Como vou rodar (localmente, quando as credenciais chegarem)

1. Configurar o gateway via `POST /empresa/gateways` (ou a tela `/gateways`).
2. Implementar o rest-client + ligar o `/testar` real.
3. Rodar `./mvnw test` (testes de integração do client, se possível com WireMock
   ou contra o sandbox) e um teste manual ponta a ponta (criar cobrança → ver
   QR/status → simular webhook).
4. Reportar resultado e seguir para `TransacaoPagamento` + UI de pagamento.

## Notas de segurança

- Segredos de gateway hoje ficam em texto puro no banco (dev). **Cifrar em
  repouso** antes de produção — ver [`../../docs/HARDENING.md`](../../docs/HARDENING.md).
