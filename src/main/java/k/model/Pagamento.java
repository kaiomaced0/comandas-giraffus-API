package k.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Pagamento extends EntityClass{

    @OneToOne
    @JoinColumn(name = "comanda_pagamento")
    private Comanda comanda;

    private Boolean pagamentoRealizado;

    @ManyToOne
    @JoinColumn(name = "usuario_caixa_pagamento")
    private Usuario caixa;

    private Double valorPagamento;

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
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
}
