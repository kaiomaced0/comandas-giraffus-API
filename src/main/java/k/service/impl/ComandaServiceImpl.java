package k.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import k.dto.CaixaDTO;
import k.dto.ComandaPagedItemDTO;
import k.dto.PagedResponse;
import k.model.*;
import k.service.CaixaService;
import k.service.PedidoService;
import org.jboss.logging.Logger;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaPagarDTO;
import k.dto.ComandaResponseDTO;
import k.repository.ComandaRepository;
import k.repository.EmpresaRepository;
import k.repository.MesaRepository;
import k.repository.PagamentoRepository;
import k.service.ComandaService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ComandaServiceImpl implements ComandaService {

    public static final Logger LOG = Logger.getLogger(ComandaServiceImpl.class);

    @Inject
    ComandaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    PagamentoRepository pagamentoRepository;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    MesaRepository mesaRepository;

    @Inject
    PedidoService pedidoService;

    @Inject
    CaixaService caixaService;

    @Override
    public List<ComandaResponseDTO> getAll() {
        try {
            LOG.info("Requisição Comandas.getAll()");
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            return emp.getComandas().stream()
                    .filter(EntityClass::getAtivo)
                    .map(ComandaResponseDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getAll()");
            return List.of();
        }
    }

    @Override
    public List<ComandaResponseDTO> getNome(String nome) {
        try {
            LOG.info("Requisição Comandas.getAll()");
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            return emp.getComandas().stream()
                    .filter(EntityClass::getAtivo).filter(comanda -> comanda.getNome().contains(nome))
                    .map(ComandaResponseDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Comandas.getAll()");
            return List.of();
        }
    }

    @Override
    public ComandaResponseDTO getId(Long id) {
        try {
            return new ComandaResponseDTO(repository.findById(id));

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Response insert(ComandaDTO comanda) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Empresa emp = usuarioLogadoService.getEmpresaLogada();
        try {
            if(emp.getCaixaAtual() == null){
                throw new Exception("Caixa atual não existe!");
            }
            Comanda entity = ComandaDTO.criaComanda(comanda);
            entity.setAtendente(u);
            entity.setPedidos(new ArrayList<>());
            if (comanda.mesaId() != null) {
                Mesa mesa = mesaRepository.findById(comanda.mesaId());
                if (mesa == null || !Boolean.TRUE.equals(mesa.getAtivo())
                        || mesa.getEmpresa() == null
                        || !mesa.getEmpresa().getId().equals(emp.getId())) {
                    throw new Exception("Mesa inválida para esta empresa");
                }
                entity.setMesa(mesa);
            }
            repository.persist(entity);
            emp.getCaixaAtual().getComandas().add(entity);
            emp.getComandas().add(entity);
            LOG.info("Requisicao Comandas.insert() - ok");
            return Response.ok().entity(new ComandaResponseDTO(entity)).build();
        } catch (Exception e) {
            LOG.error("Requisicao Comandas.insert() falhou");
            return Response.status(400).entity(e.getMessage()).build();
        }

    }

    @Override
    @Transactional
    public Response updatePreco(Long id) {
        try {

            Comanda comanda = repository.findById(id);
            comanda.setPreco(0.0);
            for (Pedido p : comanda.getPedidos()) {
                if (p.getAtivo()) {
                    pedidoService.updateValor(p.getId());
                    comanda.setPreco(comanda.getPreco() + p.getValor());
                }
            }
            return Response.ok(new ComandaResponseDTO(comanda)).build();

        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response pagar(ComandaPagarDTO comandaPagarDTO) {
        try {
            Comanda entity = repository.findById(comandaPagarDTO.id());
            entity.setFinalizada(true);
            entity.setPagamento(pagamentoRepository.findById(comandaPagarDTO.idPagamento()));
            return Response.ok().build();

        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        Comanda entity = repository.findById(id);
        entity.setAtivo(false);
        return Response.ok().build();
    }

    @Override
    public List<ComandaResponseDTO> getEmAberto() {
        try {
            LOG.info("Requisição Comandas.getEmAberto()");
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            return emp.getComandas().stream()
                    .filter(comandas -> !comandas.getFinalizada()).filter(EntityClass::getAtivo)
                    .map(ComandaResponseDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getEmAberto()");
            return List.of();
        }
    }

    @Override
    public PagedResponse<ComandaPagedItemDTO> list(
            Long mesaId,
            Boolean finalizada,
            LocalDate from,
            LocalDate to,
            Long atendenteId,
            int page,
            int size) {

        // Sanitiza paginação
        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 100);

        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return new PagedResponse<>(List.of(), p, s, 0L);
        }
        Empresa empresa = logado.getEmpresa();

        // Comanda não possui FK direta para Empresa; o mapeamento real é
        // empresa.comandas (OneToMany unidirecional). Para multi-tenant na
        // query, projetamos os ids da coleção e filtramos com IN.
        List<Long> idsEmpresa = empresa.getComandas() == null
                ? List.of()
                : empresa.getComandas().stream()
                        .filter(c -> c != null && c.getId() != null)
                        .map(Comanda::getId)
                        .collect(Collectors.toList());
        if (idsEmpresa.isEmpty()) {
            return new PagedResponse<>(List.of(), p, s, 0L);
        }

        StringBuilder ql = new StringBuilder("id in :ids and ativo = true");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ids", idsEmpresa);

        if (mesaId != null) {
            ql.append(" and mesa.id = :mesaId");
            params.put("mesaId", mesaId);
        }
        if (finalizada != null) {
            ql.append(" and finalizada = :finalizada");
            params.put("finalizada", finalizada);
        }
        if (atendenteId != null) {
            ql.append(" and atendente.id = :atendenteId");
            params.put("atendenteId", atendenteId);
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
        List<Comanda> comandas = repository
                .find(ql.toString(), Sort.by("dataInclusao").descending(), params)
                .page(Page.of(p, s))
                .list();

        List<ComandaPagedItemDTO> data = comandas.stream()
                .map(ComandaPagedItemDTO::new)
                .collect(Collectors.toList());

        return new PagedResponse<>(data, p, s, total);
    }

    @Override
    public List<ComandaResponseDTO> getAllComandasAdm(Long idEmpresa) {
        try {
            LOG.info("Requisição Comandas.getAllComandasAdm()");
            return empresaRepository.findById(idEmpresa).getComandas().stream()
                    .filter(comandas -> comandas.getAtivo())
                    .map(comandas -> new ComandaResponseDTO(comandas)).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getAllComandasAdm()");
            return null;
        }
    }

}
