# Migrações manuais (não geridas por Flyway)

Este projeto usa `hibernate.hbm2ddl.auto=update`, que **não remove** constraints
nem altera índices criados em versões anteriores. As ações abaixo precisam ser
rodadas manualmente em cada ambiente após o deploy correspondente.

## Onda F - Pagamentos múltiplos por comanda

`Pagamento.comanda` foi convertido de `@OneToOne` para `@ManyToOne`. A coluna
FK `comanda_pagamento` (apontando para `comanda.id`) já existia com restrição
UNIQUE em bancos legados. Como agora várias linhas de `pagamento` podem
referenciar a mesma comanda, é preciso remover o índice/constraint UNIQUE
manualmente:

```sql
ALTER TABLE pagamento DROP CONSTRAINT IF EXISTS pagamento_comanda_pagamento_key;
ALTER TABLE pagamento DROP CONSTRAINT IF EXISTS pagamento_comanda_id_key;
-- Em alguns bancos o índice tem nome diferente; rode também:
DROP INDEX IF EXISTS pagamento_comanda_pagamento_key;
DROP INDEX IF EXISTS pagamento_comanda_id_key;
```

Sem isso, qualquer segundo pagamento na mesma comanda falha com violação
de constraint UNIQUE.
