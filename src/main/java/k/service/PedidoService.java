package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Pedido;

public interface PedidoService {
    public List<Pedido> getAll();

    public List<Pedido> getId();

    public Response insert(Pedido pedido);

    public Response update(@PathParam("idPedido") Long idPedido, Pedido pedido);

    public Response delete(@PathParam("id") Long id);

    public Response removeItemCompra(@PathParam("id") Long id, int quantidade);
    
    public Response adicionaItemCompra(@PathParam("id") Long id, int quantidade);
}
