package k.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import k.model.enums.ModoPagamento;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class Pagamento extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "comanda_pagamento")
    private Comanda comanda;

    private Boolean pagamentoRealizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 30)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_pagamento", length = 30)
    private ModoPagamento modo;

    @ManyToOne
    @JoinColumn(name = "usuario_caixa_pagamento")
    private Usuario usuarioCaixa;

    @ManyToOne
    @JoinColumn(name = "caixa_pagamento_ref")
    private Caixa caixa;

    @Column(name = "valor_pagamento", precision = 19, scale = 2)
    private BigDecimal valorPagamento;

    @Column(name = "valor_total", precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_gorjeta", precision = 19, scale = 2)
    private BigDecimal valorGorjeta;

    @Column(nullable = false)
    private Boolean estornado = false;

    @OneToMany(mappedBy = "pagamento")
    private List<PagamentoItem> itens;

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public ModoPagamento getModo() {
        return modo;
    }

    public void setModo(ModoPagamento modo) {
        this.modo = modo;
    }

    public Boolean getPagamentoRealizado() {
        return pagamentoRealizado;
    }

    public void setPagamentoRealizado(Boolean pagamentoRealizado) {
        this.pagamentoRealizado = pagamentoRealizado;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Usuario getUsuarioCaixa() {
        return usuarioCaixa;
    }

    public void setUsuarioCaixa(Usuario usuarioCaixa) {
        this.usuarioCaixa = usuarioCaixa;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public BigDecimal getValorGorjeta() {
        return valorGorjeta;
    }

    public void setValorGorjeta(BigDecimal valorGorjeta) {
        this.valorGorjeta = valorGorjeta;
    }

    public Boolean getEstornado() {
        return estornado;
    }

    public void setEstornado(Boolean estornado) {
        this.estornado = estornado;
    }

    public List<PagamentoItem> getItens() {
        return itens;
    }

    public void setItens(List<PagamentoItem> itens) {
        this.itens = itens;
    }
}
