package k.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "pagamento_item")
@Inheritance(strategy = InheritanceType.JOINED)
public class PagamentoItem extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    @ManyToOne
    @JoinColumn(name = "item_compra_id")
    private ItemCompra itemCompra;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor_abatido", precision = 19, scale = 2)
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
