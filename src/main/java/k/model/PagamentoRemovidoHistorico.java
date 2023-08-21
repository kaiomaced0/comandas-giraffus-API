package k.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class PagamentoRemovidoHistorico extends EntityClass {

    @OneToOne
    @JoinColumn(name = "pagamento_pagamentoRemovidoHistorico")
    private Pagamento pagamento;

    @Column(name = "comentario_pagamentoRemovido")
    private String comentario;

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

}
