package k.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.PagedResponse;
import k.dto.PedidoAdicionaItemCompraDTO;
import k.dto.PedidoDTO;
import k.dto.PedidoRemoveItemCompraDTO;
import k.dto.PedidoResponseDTO;
import k.dto.PedidoStatusInputDTO;

public interface PedidoService {
    public List<PedidoResponseDTO> getAll();

    public Response getId(Long id);

    public Response updateValor(Long id);

    public List<PedidoResponseDTO> getAbertos();


    public Response insert(PedidoDTO pedido);

    public Response delete(@PathParam("id") Long id);

    public Response removeItemCompra(PedidoRemoveItemCompraDTO pedidoAddItem);

    public Response adicionaItemCompra(PedidoAdicionaItemCompraDTO pedidoRemoveItem);

    /**
     * Listagem paginada de pedidos da empresa do usuário logado.
     */
    public PagedResponse<PedidoResponseDTO> list(
            String status,
            Long comandaId,
            LocalDate from,
            LocalDate to,
            int page,
            int size);

    /**
     * Atualiza o status de um pedido, validando a transição.
     * Lança {@link jakarta.ws.rs.WebApplicationException} 422 quando a
     * transição não é permitida.
     */
    public PedidoResponseDTO atualizarStatus(Long id, PedidoStatusInputDTO dto);
}
