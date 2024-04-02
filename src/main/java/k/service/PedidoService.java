package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.PedidoAdicionaItemCompraDTO;
import k.dto.PedidoDTO;
import k.dto.PedidoRemoveItemCompraDTO;
import k.dto.PedidoResponseDTO;

public interface PedidoService {
    public List<PedidoResponseDTO> getAll();

    public Response getId(Long id);

    public Response updateValor(Long id);

    public List<PedidoResponseDTO> getAbertos();


    public Response insert(PedidoDTO pedido);

    public Response delete(@PathParam("id") Long id);

    public Response removeItemCompra(PedidoRemoveItemCompraDTO pedidoAddItem);

    public Response adicionaItemCompra(PedidoAdicionaItemCompraDTO pedidoRemoveItem);
}
