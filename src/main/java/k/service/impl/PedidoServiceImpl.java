package k.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.*;
import k.model.*;
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
            Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
            Comanda comanda = comandaRepository.findById(pedidoDTO.idComanda());
            if (!u.getEmpresa().getComandas().contains(comanda)) {
                throw new Exception();
            }
            Pedido pedido = new Pedido();
            pedido.setValor(0.0);
            for (ItemCompraDTO i : pedidoDTO.listItemCompraDTO()) {
                ItemCompra item = new ItemCompra();
                item = itemCompraService.insert(i);
                if(item != null){
                    if(pedido.getItemCompras() == null){
                        pedido.setItemCompras(new ArrayList<>());
                    }
                    pedido.getItemCompras().add(item);
                    pedido.setValor(pedido.getValor() + item.getPreco());
                }
            }
            pedido.setObservacao(pedidoDTO.observacao());
            pedido.setQuantidadePessoas(pedidoDTO.quantidadePessoas());
            pedido.setStatusPedido(StatusPedido.AGUARDANDO);
            pedido.setComanda(comanda);
            repository.persist(pedido);
            if(comanda.getPedidos() == null){
                comanda.setPedidos(new ArrayList<>());
            }
            comanda.getPedidos().add(pedido);
            comandaService.updatePreco(comanda.getId());
            return Response.ok(new PedidoResponseDTO(pedido)).build();

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

    @Override
    public PagedResponse<PedidoResponseDTO> list(
            String status,
            Long comandaId,
            LocalDate from,
            LocalDate to,
            int page,
            int size) {

        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 100);

        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return new PagedResponse<>(List.of(), p, s, 0L);
        }
        Empresa empresa = logado.getEmpresa();

        // Multi-tenant: pedido pertence a comanda da empresa
        List<Long> idsComandasEmpresa = empresa.getComandas() == null
                ? List.of()
                : empresa.getComandas().stream()
                        .filter(c -> c != null && c.getId() != null)
                        .map(Comanda::getId)
                        .collect(Collectors.toList());
        if (idsComandasEmpresa.isEmpty()) {
            return new PagedResponse<>(List.of(), p, s, 0L);
        }

        StringBuilder ql = new StringBuilder("comanda.id in :ids and ativo = true");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ids", idsComandasEmpresa);

        if (status != null && !status.isBlank()) {
            StatusPedido sp;
            try {
                sp = StatusPedido.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(
                        "status invalido (esperado AGUARDANDO|PREPARANDO|PRONTO|ENTREGUE): "
                                + status,
                        Response.Status.BAD_REQUEST);
            }
            ql.append(" and statusPedido = :status");
            params.put("status", sp);
        }
        if (comandaId != null) {
            // garante que comandaId pertença à empresa
            if (!idsComandasEmpresa.contains(comandaId)) {
                return new PagedResponse<>(List.of(), p, s, 0L);
            }
            ql.append(" and comanda.id = :comandaId");
            params.put("comandaId", comandaId);
        }
        if (from != null) {
            ql.append(" and dataInclusao >= :from");
            params.put("from", from.atStartOfDay());
        }
        if (to != null) {
            ql.append(" and dataInclusao <= :to");
            params.put("to", to.atTime(LocalTime.MAX));
        }

        long total = repository.count(ql.toString(), params);
        List<Pedido> pedidos = repository
                .find(ql.toString(), Sort.by("dataInclusao").descending(), params)
                .page(Page.of(p, s))
                .list();

        List<PedidoResponseDTO> data = pedidos.stream()
                .map(PedidoResponseDTO::new)
                .collect(Collectors.toList());

        return new PagedResponse<>(data, p, s, total);
    }

    /**
     * Máquina de transição de status do pedido.
     * <p>
     * Permitidas:
     * <ul>
     *   <li>AGUARDANDO -> PREPARANDO</li>
     *   <li>PREPARANDO -> PRONTO</li>
     *   <li>PRONTO -> ENTREGUE</li>
     * </ul>
     * Qualquer outra transição -> 422.
     * <p>
     * TODO Fase 2: persistir histórico (ver docs/api-roadmap.md).
     */
    @Override
    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, PedidoStatusInputDTO dto) {
        if (dto == null || dto.status() == null || dto.status().isBlank()) {
            throw new WebApplicationException(
                    "Body com 'status' obrigatorio", Response.Status.BAD_REQUEST);
        }

        StatusPedido novo;
        try {
            novo = StatusPedido.valueOf(dto.status().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    "status invalido (esperado AGUARDANDO|PREPARANDO|PRONTO|ENTREGUE): "
                            + dto.status(),
                    Response.Status.BAD_REQUEST);
        }

        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            throw new WebApplicationException("Usuario nao autenticado",
                    Response.Status.UNAUTHORIZED);
        }
        if (id == null) {
            throw new NotFoundException("Pedido nao encontrado");
        }
        Pedido pedido = repository.findById(id);
        if (pedido == null || !Boolean.TRUE.equals(pedido.getAtivo())) {
            throw new NotFoundException("Pedido nao encontrado");
        }

        // Multi-tenant: comanda do pedido deve pertencer à empresa do logado
        Empresa empresa = logado.getEmpresa();
        Comanda comanda = pedido.getComanda();
        if (comanda == null || empresa.getComandas() == null
                || empresa.getComandas().stream()
                        .noneMatch(c -> c != null && c.getId() != null
                                && c.getId().equals(comanda.getId()))) {
            throw new NotFoundException("Pedido nao encontrado");
        }

        StatusPedido atual = pedido.getStatusPedido();
        if (!isTransicaoValida(atual, novo)) {
            throw new WebApplicationException(
                    "Transicao de status invalida: "
                            + (atual == null ? "(nulo)" : atual.name())
                            + " -> " + novo.name(),
                    422);
        }

        pedido.setStatusPedido(novo);
        LOG.info("Pedido id=" + pedido.getId()
                + " status alterado de " + (atual == null ? "(nulo)" : atual.name())
                + " para " + novo.name()
                + " por usuario=" + logado.getLogin());

        return new PedidoResponseDTO(pedido);
    }

    private static boolean isTransicaoValida(StatusPedido atual, StatusPedido novo) {
        if (novo == null) {
            return false;
        }
        if (atual == null) {
            // nunca seteado; trate como AGUARDANDO inicial — só permite avançar para PREPARANDO
            return novo == StatusPedido.PREPARANDO;
        }
        switch (atual) {
            case AGUARDANDO:
                return novo == StatusPedido.PREPARANDO;
            case PREPARANDO:
                return novo == StatusPedido.PRONTO;
            case PRONTO:
                return novo == StatusPedido.ENTREGUE;
            case ENTREGUE:
            default:
                return false;
        }
    }

}
