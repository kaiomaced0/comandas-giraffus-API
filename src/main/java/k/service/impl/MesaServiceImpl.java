package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.MesaComandaDTO;
import k.dto.MesaInputDTO;
import k.dto.MesaResponseDTO;
import k.model.Comanda;
import k.model.Empresa;
import k.model.Mesa;
import k.repository.MesaRepository;
import k.service.MesaService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class MesaServiceImpl implements MesaService {

    public static final Logger LOG = Logger.getLogger(MesaServiceImpl.class);

    @Inject
    MesaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<MesaResponseDTO> getAll() {
        Empresa empresa = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        return repository.findByEmpresa(empresa).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MesaResponseDTO getById(Long id) {
        Mesa entity = findOwned(id);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public MesaResponseDTO insert(MesaInputDTO dto) {
        if (dto == null || dto.identificador() == null || dto.identificador().isBlank()) {
            throw new WebApplicationException("Identificador é obrigatório", Response.Status.BAD_REQUEST);
        }
        Empresa empresa = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        validateUnique(empresa, dto.identificador().trim(), null);

        Mesa entity = new Mesa();
        entity.setIdentificador(dto.identificador().trim());
        entity.setCapacidade(dto.capacidade() == null ? 4 : dto.capacidade());
        entity.setEmpresa(empresa);
        repository.persist(entity);
        LOG.info("Mesa criada id=" + entity.getId());
        return toResponse(entity);
    }

    @Override
    @Transactional
    public MesaResponseDTO update(Long id, MesaInputDTO dto) {
        Mesa entity = findOwned(id);
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        if (dto.identificador() != null && !dto.identificador().isBlank()) {
            String novo = dto.identificador().trim();
            if (!novo.equalsIgnoreCase(entity.getIdentificador())) {
                validateUnique(entity.getEmpresa(), novo, entity.getId());
            }
            entity.setIdentificador(novo);
        }
        if (dto.capacidade() != null) {
            entity.setCapacidade(dto.capacidade());
        }
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Mesa entity = findOwned(id);
        entity.setAtivo(false);
    }

    @Override
    public List<MesaComandaDTO> getComandasAbertas(Long mesaId) {
        Mesa mesa = findOwned(mesaId);
        Empresa empresa = mesa.getEmpresa();
        if (empresa == null || empresa.getComandas() == null) {
            return List.of();
        }
        return empresa.getComandas().stream()
                .filter(c -> c != null && Boolean.TRUE.equals(c.getAtivo()))
                .filter(c -> Boolean.FALSE.equals(c.getFinalizada()))
                .filter(c -> c.getMesa() != null && mesa.getId().equals(c.getMesa().getId()))
                .map(this::toComandaDTO)
                .collect(Collectors.toList());
    }

    private Mesa findOwned(Long id) {
        Mesa entity = repository.findById(id);
        if (entity == null || !Boolean.TRUE.equals(entity.getAtivo())) {
            throw new NotFoundException("Mesa não encontrada");
        }
        Empresa empresaLogada = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        if (entity.getEmpresa() == null || empresaLogada == null
                || !entity.getEmpresa().getId().equals(empresaLogada.getId())) {
            throw new NotFoundException("Mesa não encontrada");
        }
        return entity;
    }

    private void validateUnique(Empresa empresa, String identificador, Long ignoreId) {
        boolean conflito = repository.findByEmpresa(empresa).stream()
                .filter(m -> ignoreId == null || !ignoreId.equals(m.getId()))
                .anyMatch(m -> identificador.equalsIgnoreCase(m.getIdentificador()));
        if (conflito) {
            throw new WebApplicationException(
                    "Já existe mesa com identificador '" + identificador + "' nesta empresa",
                    Response.Status.CONFLICT);
        }
    }

    private MesaResponseDTO toResponse(Mesa m) {
        return new MesaResponseDTO(m.getId(), m.getIdentificador(), m.getCapacidade(), m.getAtivo());
    }

    private MesaComandaDTO toComandaDTO(Comanda c) {
        return new MesaComandaDTO(c.getId(), c.getNome(), c.getPreco(), c.getFinalizada());
    }
}
