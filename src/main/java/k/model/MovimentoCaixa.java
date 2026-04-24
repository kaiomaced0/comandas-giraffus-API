package k.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Where;

import k.model.enums.TipoMovimentoCaixa;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class MovimentoCaixa extends EntityClass {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", length = 30, nullable = false)
    private TipoMovimentoCaixa tipo;

    @ManyToOne
    @JoinColumn(name = "caixa_movimento", nullable = false)
    private Caixa caixa;

    @ManyToOne
    @JoinColumn(name = "caixa_destino_movimento")
    private Caixa caixaDestino;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(length = 500)
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "usuario_movimento")
    private Usuario usuario;

    @Column(name = "data_movimento")
    private LocalDateTime data;

    public TipoMovimentoCaixa getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoCaixa tipo) {
        this.tipo = tipo;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public Caixa getCaixaDestino() {
        return caixaDestino;
    }

    public void setCaixaDestino(Caixa caixaDestino) {
        this.caixaDestino = caixaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
