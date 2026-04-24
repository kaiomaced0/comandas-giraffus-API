package k.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Where;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class PagamentoItem extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "pagamento_item", nullable = false)
    private Pagamento pagamento;

    @ManyToOne
    @JoinColumn(name = "itemcompra_pagamentoitem", nullable = false)
    private ItemCompra itemCompra;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_abatido", precision = 19, scale = 2, nullable = false)
    private BigDecimal valorAbatido;

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public ItemCompra getItemCompra() {
        return itemCompra;
    }

    public void setItemCompra(ItemCompra itemCompra) {
        this.itemCompra = itemCompra;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorAbatido() {
        return valorAbatido;
    }

    public void setValorAbatido(BigDecimal valorAbatido) {
        this.valorAbatido = valorAbatido;
    }
}
