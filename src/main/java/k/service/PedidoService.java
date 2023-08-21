package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.PedidoAdicionaRemoveDTO;
import k.dto.PedidoDTO;
import k.dto.PedidoResponseDTO;

public interface PedidoService {
    public List<PedidoResponseDTO> getAll();

    public Response getId(Long id);

    public Response insert(PedidoDTO pedido);

    public Response delete(@PathParam("id") Long id);

    public Response removeItemCompra(PedidoAdicionaRemoveDTO pedidoAdicionaRemoveDTO);

    public Response adicionaItemCompra(PedidoAdicionaRemoveDTO pedidoAdicionaRemoveDTO);
}
