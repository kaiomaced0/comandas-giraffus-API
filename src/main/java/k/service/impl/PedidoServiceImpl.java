package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.ws.rs.core.Response;
import k.dto.PedidoAdicionaRemoveDTO;
import k.dto.PedidoDTO;
import k.dto.PedidoResponseDTO;
import k.model.Comanda;
import k.model.ItemCompra;
import k.model.Pedido;
import k.model.StatusPedido;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PedidoRepository;
import k.service.PedidoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PedidoServiceImpl implements PedidoService {

    @Inject
    PedidoRepository repository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    ComandaRepository comandaRepository;

    @Override
    public List<PedidoResponseDTO> getAll() {
        return repository.findAll().stream()
                .filter(pedido -> pedido.getComanda().getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa())
                .map(pedido -> new PedidoResponseDTO(pedido)).collect(Collectors.toList());

    }

    @Override
    public Response getId(Long id) {
        Pedido p = repository.findById(id);
        try {

            if (p.getComanda().getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
                return Response.ok(new PedidoResponseDTO(p)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    public Response insert(PedidoDTO pedidoDTO) {
        try {

            Comanda comanda = comandaRepository.findById(pedidoDTO.idComanda());
            if (comanda.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
                Pedido pedido = new Pedido();
                for (Long i : pedidoDTO.listIdItemCompra()) {
                    pedido.getItemCompras().add(itemCompraRepository.findById(i));
                }
                pedido.setObservacao(pedidoDTO.observacao());
                pedido.setQuantidadePessoas(pedidoDTO.quantidadePessoas());
                pedido.setStatusPedido(StatusPedido.AGUARDANDO);
                pedido.setComanda(comanda);
                repository.persist(pedido);
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    public Response delete(Long id) {
        try {

            Pedido pedido = repository.findById(id);
            if (pedido.getComanda().getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {

                repository.deleteById(id);
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    public Response removeItemCompra(PedidoAdicionaRemoveDTO pedidoAdicionaRemoveDTO) {
        try {
            Pedido entity = repository.findById(pedidoAdicionaRemoveDTO.idPedido());
            ItemCompra i = itemCompraRepository.findById(pedidoAdicionaRemoveDTO.idItemCompra());
            entity.getItemCompras().remove(i);
            itemCompraRepository.delete(i);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    public Response adicionaItemCompra(PedidoAdicionaRemoveDTO pedidoAdicionaRemoveDTO) {
        try {
            Pedido entity = repository.findById(pedidoAdicionaRemoveDTO.idPedido());
            entity.getItemCompras().add(itemCompraRepository.findById(pedidoAdicionaRemoveDTO.idItemCompra()));
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

}
