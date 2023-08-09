package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaResponseDTO;
import k.model.Comanda;
import k.repository.ComandaRepository;
import k.repository.EmpresaRepository;
import k.repository.PagamentoRepository;
import k.service.ComandaService;
import k.service.UsuarioLogadoService;

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
        return new ComandaResponseDTO(repository.findByNome(nome));
    }

    @Override
    public ComandaResponseDTO getId(Long id) {
        return new ComandaResponseDTO(repository.findById(id));
    }

    @Override
    public Response insert(ComandaDTO comanda) {
        Comanda entity = ComandaDTO.criaComanda(comanda);
        entity.setAtendente(usuarioLogadoService.getPerfilUsuarioLogado());
        return Response.ok().build();

    }

    @Override
    public Response pagar(Long id, Long idPagamento) {
        Comanda entity = repository.findById(id);
        entity.setFinalizada(true);
        entity.setPagamento(pagamentoRepository.findById(idPagamento));
        return Response.ok().build();
    }

    @Override
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
