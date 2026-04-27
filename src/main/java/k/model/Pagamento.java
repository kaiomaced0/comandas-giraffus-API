package k.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Pagamento extends EntityClass {

    // Onda F: era @OneToOne (uma comanda -> um pagamento). Agora @ManyToOne para
    // permitir múltiplos pagamentos por comanda.
    // ATENÇÃO: a coluna comanda_pagamento (FK comanda_id) já existe com constraint UNIQUE
    // em bancos legados. Hibernate hbm2ddl=update NÃO remove esse índice unique
    // automaticamente. O operador deve rodar manualmente:
    //   ALTER TABLE pagamento DROP CONSTRAINT IF EXISTS pagamento_comanda_pagamento_key;
    //   ALTER TABLE pagamento DROP CONSTRAINT IF EXISTS pagamento_comanda_id_key;
    // Ver docs/manual-migrations.md.
    @ManyToOne
    @JoinColumn(name = "comanda_pagamento")
    private Comanda comanda;

    private Boolean pagamentoRealizado;

    @JoinColumn(name = "forma_pagamento")
    private FormaPagamento formaPagamento;

    @ManyToOne
    @JoinColumn(name = "usuario_caixa_pagamento")
    private Usuario usuarioCaixa;

    private Double valorPagamento;

    private Double valorGorjeta;

    // ---------------- Onda F: campos novos ----------------

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_pagamento", length = 20)
    private ModoPagamento modo = ModoPagamento.SIMPLES;

    @ManyToOne
    @JoinColumn(name = "caixa_pagamento_ref")
    private Caixa caixa;

    @Column(name = "valor_total_pagamento", precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "estornado")
    private Boolean estornado = false;

    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagamentoItem> itens = new ArrayList<>();

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

    public Boolean getPagamentoRealizado() {
        return pagamentoRealizado;
    }

    public void setPagamentoRealizado(Boolean pagamentoRealizado) {
        this.pagamentoRealizado = pagamentoRealizado;
    }

    public Double getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(Double valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public Usuario getUsuarioCaixa() {
        return usuarioCaixa;
    }

    public void setUsuarioCaixa(Usuario usuarioCaixa) {
        this.usuarioCaixa = usuarioCaixa;
    }

    public Double getValorGorjeta() {
        return valorGorjeta;
    }

    public void setValorGorjeta(Double valorGorjeta) {
        this.valorGorjeta = valorGorjeta;
    }

    public ModoPagamento getModo() {
        return modo;
    }

    public void setModo(ModoPagamento modo) {
        this.modo = modo;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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
