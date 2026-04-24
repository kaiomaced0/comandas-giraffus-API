package k.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaResponseDTO;
import k.exception.BusinessException;
import k.model.Caixa;
import k.model.Comanda;
import k.model.Mesa;
import k.model.Pedido;
import k.model.Usuario;
import k.repository.CaixaRepository;
import k.repository.ComandaRepository;
import k.repository.EmpresaRepository;
import k.repository.MesaRepository;
import k.service.ComandaService;
import k.service.PedidoService;
import k.service.UsuarioLogadoService;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ComandaServiceImpl implements ComandaService {

    public static final Logger LOG = Logger.getLogger(ComandaServiceImpl.class);

    @Inject
    ComandaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    PedidoService pedidoService;

    @Inject
    CaixaRepository caixaRepository;

    @Inject
    MesaRepository mesaRepository;

    @Override
    public List<ComandaResponseDTO> getAll() {
        try {
            LOG.info("Requisição Comandas.getAll()");
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas().stream()
                    .map(ComandaResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getAll()");
            return null;
        }
    }

    @Override
    public List<ComandaResponseDTO> getNome(String nome) {
        try {
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas().stream()
                    .filter(c -> c.getNome() != null && c.getNome().contains(nome))
                    .map(ComandaResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Comandas.getNome()");
            return null;
        }
    }

    @Override
    public ComandaResponseDTO getId(Long id) {
        try {
            Comanda c = repository.findById(id);
            return c == null ? null : new ComandaResponseDTO(c);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Response insert(ComandaDTO comanda) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        try {
            Caixa caixaAberto = caixaRepository.findAbertoPorUsuario(u);
            if (caixaAberto == null) {
                throw new BusinessException("Usuário não tem caixa aberto; abra um caixa antes de criar comandas");
            }
            Comanda entity = ComandaDTO.criaComanda(comanda);
            entity.setAtendente(u);
            entity.setPedidos(new ArrayList<>());
            if (comanda.mesaId() != null) {
                Mesa mesa = mesaRepository.findById(comanda.mesaId());
                if (mesa == null || mesa.getEmpresa() == null
                        || !mesa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
                    throw new BusinessException("Mesa não encontrada na empresa");
                }
                entity.setMesa(mesa);
            }
            repository.persist(entity);
            if (caixaAberto.getComandas() == null) {
                caixaAberto.setComandas(new ArrayList<>());
            }
            caixaAberto.getComandas().add(entity);
            if (u.getEmpresa().getComandas() == null) {
                u.getEmpresa().setComandas(new ArrayList<>());
            }
            u.getEmpresa().getComandas().add(entity);
            return Response.ok(new ComandaResponseDTO(entity)).build();
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            LOG.error("Requisicao Comandas.insert() falhou", e);
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @Override
    @Transactional
    public Response updatePreco(Long id) {
        try {
            Comanda comanda = repository.findById(id);
            comanda.setPreco(0.0);
            if (comanda.getPedidos() != null) {
                for (Pedido p : comanda.getPedidos()) {
                    pedidoService.updateValor(p.getId());
                    if (p.getValor() != null) {
                        comanda.setPreco(comanda.getPreco() + p.getValor());
                    }
                }
            }
            return Response.ok(new ComandaResponseDTO(comanda)).build();
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
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas().stream()
                    .filter(c -> !Boolean.TRUE.equals(c.getFinalizada()))
                    .map(ComandaResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ComandaResponseDTO> getAllComandasAdm(Long idEmpresa) {
        try {
            return empresaRepository.findById(idEmpresa).getComandas().stream()
                    .map(ComandaResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }
}
