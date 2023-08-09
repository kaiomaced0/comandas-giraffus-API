package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.dto.PedidoDTO;
import k.dto.PedidoResponseDTO;
import k.model.Pedido;
import k.model.StatusPedido;
import k.repository.ItemCompraRepository;
import k.repository.PedidoRepository;
import k.service.PedidoService;

public class PedidoServiceImpl implements PedidoService {

    @Inject
    PedidoRepository repository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Override
    public List<PedidoResponseDTO> getAll() {
        return repository.findAll().stream().map(pedido -> new PedidoResponseDTO(pedido)).collect(Collectors.toList());

    }

    @Override
    public PedidoResponseDTO getId(Long id) {
        return new PedidoResponseDTO(repository.findById(id));
    }

    @Override
    public Response insert(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        for (Long i : pedidoDTO.listIdItemCompra()) {
            pedido.getItemCompras().add(itemCompraRepository.findById(i));
        }
        pedido.setObservacao(pedidoDTO.observacao());
        pedido.setQuantidadePessoas(pedidoDTO.quantidadePessoas());
        pedido.setStatusPedido(StatusPedido.AGUARDANDO);
        return Response.ok().build();
    }

    @Override
    public Response delete(Long id) {
        repository.deleteById(id);
        return Response.ok().build();

    }

    @Override
    public Response removeItemCompra(Long idPedido, Long idItemCompra) {
        Pedido entity = new Pedido();
        entity.getItemCompras().remove(itemCompraRepository.findById(idItemCompra));
        return Response.ok().build();
    }

    @Override
    public Response adicionaItemCompra(Long idPedido, Long idItemCompra) {
        Pedido entity = new Pedido();
        entity.getItemCompras().add(itemCompraRepository.findById(idItemCompra));
        return Response.ok().build();
    }

}
