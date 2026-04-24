package k.model;

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

import k.model.enums.TipoMovimentoEstoque;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class MovimentoEstoque extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "produto_movimentoestoque", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", length = 30, nullable = false)
    private TipoMovimentoEstoque tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 500)
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "usuario_movimentoestoque")
    private Usuario usuario;

    @Column(name = "data_movimento")
    private LocalDateTime data;

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public TipoMovimentoEstoque getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoEstoque tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
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
