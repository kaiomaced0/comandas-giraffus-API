# API Roadmap

Documento vivo com decisões e itens postergados para próximas fases.

## Fase 1 — Paginação por offset + Filtros + Status de pedido (entregue)

- DTO genérico `k.dto.PagedResponse<T>` com `data, page, size, total`.
- Endpoints com `?page=&size=` (defaults: page=0, size=20, max size=100):
  - `GET /comanda/page` — filtros: `mesaId`, `finalizada`, `from`, `to`, `atendenteId`
  - `GET /pedido/page`  — filtros: `status`, `comandaId`, `from`, `to`
  - `GET /produto/page` — filtros: `tipoProdutoId`, `search`, `emEstoque`
  - `GET /pagamento/page` — filtros: `caixaId`, `formaPagamento`, `from`, `to`, `usuarioId`
- Multi-tenant em todos os listings (filtra pela empresa do logado).
- Endpoints antigos (`GET /comanda`, `GET /pedido`, etc.) continuam funcionando — não foram removidos para preservar clientes existentes.
- `PATCH /pedido/{id}/status` com máquina de transição:
  - AGUARDANDO -> PREPARANDO -> PRONTO -> ENTREGUE
  - Qualquer outra transição retorna 422.

## TODO — Fase 2

### Cursor pagination (mobile)

Para clientes mobile (scroll infinito), criar um `CursorResponse<T>` com
`data, nextCursor, prevCursor` baseado em `(dataInclusao, id)` codificado em
base64. Mais estável do que offset quando há inserts concorrentes, e tira a
necessidade do `count(*)` (que é caro em tabelas grandes).

Endpoints candidatos: `GET /pedido/cursor`, `GET /comanda/cursor`,
`GET /pagamento/cursor`. O endpoint `?page=` permanece para backoffice.

### Histórico de transição de status do pedido (opcional)

Marcado como opcional em `TASKS.md`. Caso seja necessário (ex.: auditoria,
reabertura de pedidos), criar:

- `k.model.PedidoStatusHistorico` com `pedido, statusAnterior, statusNovo,
  usuario, momento, observacao`.
- Endpoint `GET /pedido/{id}/historico`.
- Persistir no `PedidoServiceImpl.atualizarStatus(...)` antes do `setStatusPedido`.

Por ora, NÃO foi implementado.

### Outros itens deferidos

- Ordenação configurável (`?sort=campo,asc|desc`) — atualmente fixo em
  `dataInclusao DESC`.
- Sparse fieldsets (`?fields=...`) — fora de escopo da Fase 1.
- Streaming (`GET .../stream` SSE para pedidos da cozinha) — depende da Fase 2.
