package k.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.*;
import k.model.Comanda;
import k.model.ItemCompra;
import k.model.Pedido;
import k.model.StatusPedido;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PedidoRepository;
import k.service.ComandaService;
import k.service.ItemCompraService;
import k.service.PedidoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PedidoServiceImpl implements PedidoService {

    public static final Logger LOG = Logger.getLogger(PedidoServiceImpl.class);

    @Inject
    PedidoRepository repository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    ItemCompraService itemCompraService;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    ComandaService comandaService;


    @Override
    public List<PedidoResponseDTO> getAll() {
        return repository.findAll().stream()
                .filter(pedido -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getComandas().contains(pedido.getComanda()))
                .map(PedidoResponseDTO::new).collect(Collectors.toList());

    }

    @Override
    public Response getId(Long id) {
        Pedido p = repository.findById(id);
        try {

            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas()
                    .contains(p.getComanda())) {
                return Response.ok(new PedidoResponseDTO(p)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response insert(PedidoDTO pedidoDTO) {
        try {

            Comanda comanda = comandaRepository.findById(pedidoDTO.idComanda());
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas().contains(comanda)) {
                Pedido pedido = new Pedido();
                pedido.setValor(0.0);
                for (ItemCompraDTO i : pedidoDTO.listItemCompraDTO()) {
                    ItemCompra item = new ItemCompra();
                    item = itemCompraService.insert(i);
                    if(item != null){
                        pedido.getItemCompras().add(item);
                        pedido.setValor(pedido.getValor() + item.getPreco());
                    }
                }
                pedido.setObservacao(pedidoDTO.observacao());
                pedido.setQuantidadePessoas(pedidoDTO.quantidadePessoas());
                pedido.setStatusPedido(StatusPedido.AGUARDANDO);
                pedido.setComanda(comanda);
                repository.persist(pedido);
                comandaService.updatePreco(comanda.getId());
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        try {

            Pedido pedido = repository.findById(id);
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas()
                    .contains(pedido.getComanda())) {

                repository.deleteById(id);
                comandaService.updatePreco(pedido.getComanda().getId());
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    @Transactional
    public Response removeItemCompra(PedidoRemoveItemCompraDTO pedidoRemoveDTO) {
        try {
            Pedido entity = repository.findById(pedidoRemoveDTO.id());
            ItemCompra i = itemCompraRepository.findById(pedidoRemoveDTO.itemCompra());
            entity.getItemCompras().remove(i);
            itemCompraRepository.delete(i);
            updateValor(entity.getId());
            comandaService.updatePreco(entity.getId());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response adicionaItemCompra(PedidoAdicionaItemCompraDTO pedidoAddItemDTO) {
        try {
            Pedido entity = repository.findById(pedidoAddItemDTO.id());
            entity.getItemCompras().add(itemCompraService.insert(pedidoAddItemDTO.itemCompra()));
            updateValor(entity.getId());
            comandaService.updatePreco(entity.getId());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response updateValor(Long id) {
        try {

            Pedido pedido = repository.findById(id);
            pedido.setValor(0.0);
            for (ItemCompra i : pedido.getItemCompras()) {
                if (i.getAtivo()) {
                    i.setPreco(pedido.getValor() + i.getPreco());
                }
            }
            return Response.ok(new PedidoResponseDTO(pedido)).build();

        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    public List<PedidoResponseDTO> getAbertos() {
        return repository.findAll().stream()
                .filter(pedido -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getComandas().contains(pedido.getComanda()))
                .filter(pedido -> !pedido.getComanda().getFinalizada()).
                filter(pedido -> usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixaAtual().getComandas().contains(pedido.getComanda()))
                .map(PedidoResponseDTO::new).collect(Collectors.toList());

    }

}
