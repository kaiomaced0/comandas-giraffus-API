package k.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Pedido extends EntityClass {

    @OneToMany
    @JoinColumn(name = "lista_itemcompra_pedido")
    private List<ItemCompra> itemCompras;

    private String observacao;

    @Column(name = "status_pedido")
    private StatusPedido statusPedido;

    private Integer quantidadePessoas;

    @ManyToOne
    @JoinColumn(name = "comanda_pedido")
    private Comanda comanda;

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public List<ItemCompra> getItemCompras() {
        return itemCompras;
    }

    public void setItemCompras(List<ItemCompra> itemCompras) {
        this.itemCompras = itemCompras;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public void setStatusPedido(StatusPedido statusPedido) {
        this.statusPedido = statusPedido;
    }

    public Integer getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }

}
