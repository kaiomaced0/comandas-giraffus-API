package k.model;

import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Where(clause = "ativo = true")
public class Comanda extends EntityClass {

    private String nome;

    @OneToMany
    @JoinColumn(name = "lista_pedido_comanda")
    private List<Pedido> pedidos;

    private Double preco;

    private Boolean finalizada;

    @OneToMany(mappedBy = "comanda")
    private List<Pagamento> pagamentos;

    @ManyToOne
    @JoinColumn(name = "atendente_comanda")
    private Usuario atendente;

    @ManyToOne
    @JoinColumn(name = "mesa_comanda")
    private Mesa mesa;

    private Boolean taxaServico;

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Boolean getTaxaServico() {
        return taxaServico;
    }

    public void setTaxaServico(Boolean taxaServico) {
        this.taxaServico = taxaServico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Boolean getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Usuario getAtendente() {
        return atendente;
    }

    public void setAtendente(Usuario atendente) {
        this.atendente = atendente;
    }
}
