package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaPagarDTO;
import k.dto.ComandaResponseDTO;
import k.model.Comanda;
import k.model.ItemCompra;
import k.model.Pedido;
import k.repository.ComandaRepository;
import k.repository.EmpresaRepository;
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

    @Override
    public List<ComandaResponseDTO> getAll() {
        try {
            LOG.info("Requisição Comandas.getAll()");
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas().stream()
                    .filter(comandas -> comandas.getAtivo())
                    .map(comandas -> new ComandaResponseDTO(comandas)).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getAll()");
            return null;
        }
    }

    @Override
    public ComandaResponseDTO getNome(String nome) {
        try {
            return new ComandaResponseDTO(repository.findByNome(nome));
        } catch (Exception e) {
            return null;
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
        try {
            Comanda entity = ComandaDTO.criaComanda(comanda);
            entity.setAtendente(usuarioLogadoService.getPerfilUsuarioLogado());
            repository.persist(entity);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    @Transactional
    public Response updatePreco(Long id) {
        try {

            Comanda comanda = repository.findById(id);
            comanda.setPreco(0.0);
            for (Pedido p : comanda.getPedidos()) {
                if (p.getAtivo() == true) {
                    for (ItemCompra i : p.getItemCompras()) {
                        comanda.setPreco(comanda.getPreco() + i.getPreco());
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
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas().stream()
                    .filter(comandas -> !comandas.getFinalizada()).filter(comandas -> comandas.getAtivo())
                    .map(comandas -> new ComandaResponseDTO(comandas)).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getEmAberto()");
            return null;
        }
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
