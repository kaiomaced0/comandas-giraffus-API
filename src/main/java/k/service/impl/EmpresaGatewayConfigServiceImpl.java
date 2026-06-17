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
import k.dto.GatewayConfigInputDTO;
import k.dto.GatewayConfigResponseDTO;
import k.dto.GatewayTesteResponseDTO;
import k.model.AmbienteGateway;
import k.model.Empresa;
import k.model.EmpresaGatewayConfig;
import k.model.TipoGateway;
import k.model.Usuario;
import k.repository.EmpresaGatewayConfigRepository;
import k.service.EmpresaGatewayConfigService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class EmpresaGatewayConfigServiceImpl implements EmpresaGatewayConfigService {

    public static final Logger LOG = Logger.getLogger(EmpresaGatewayConfigServiceImpl.class);

    @Inject
    EmpresaGatewayConfigRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<GatewayConfigResponseDTO> getAll() {
        Empresa empresa = empresaLogada();
        if (empresa == null) {
            return List.of();
        }
        return repository.findByEmpresa(empresa).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GatewayConfigResponseDTO insert(GatewayConfigInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        Empresa empresa = empresaLogada();
        if (empresa == null) {
            throw new WebApplicationException("Usuário sem empresa associada", Response.Status.BAD_REQUEST);
        }

        EmpresaGatewayConfig entity = new EmpresaGatewayConfig();
        entity.setEmpresa(empresa);
        entity.setTipo(parseTipo(dto.tipo()));
        entity.setAmbiente(parseAmbiente(dto.ambiente()));
        // TODO produção: cifrar apiKey/apiSecret em repouso (Onda N)
        entity.setApiKey(trimToNull(dto.apiKey()));
        entity.setApiSecret(trimToNull(dto.apiSecret()));
        entity.setHabilitado(dto.habilitado() != null && dto.habilitado());

        repository.persist(entity);
        LOG.info("Gateway config criada id=" + entity.getId() + " tipo=" + entity.getTipo());
        return toResponse(entity);
    }

    @Override
    @Transactional
    public GatewayConfigResponseDTO update(Long id, GatewayConfigInputDTO dto) {
        EmpresaGatewayConfig entity = findOwned(id);
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        if (dto.tipo() != null) {
            entity.setTipo(parseTipo(dto.tipo()));
        }
        if (dto.ambiente() != null) {
            entity.setAmbiente(parseAmbiente(dto.ambiente()));
        }
        // TODO produção: cifrar apiKey/apiSecret em repouso (Onda N)
        if (dto.apiKey() != null) {
            entity.setApiKey(trimToNull(dto.apiKey()));
        }
        if (dto.apiSecret() != null) {
            entity.setApiSecret(trimToNull(dto.apiSecret()));
        }
        if (dto.habilitado() != null) {
            entity.setHabilitado(dto.habilitado());
        }
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EmpresaGatewayConfig entity = findOwned(id);
        entity.setAtivo(false);
    }

    @Override
    public GatewayTesteResponseDTO testar(Long id) {
        EmpresaGatewayConfig entity = findOwned(id);
        // STUB: sem chamada HTTP externa. Apenas valida se a configuração está
        // completa o suficiente para uma futura conexão real.
        if (entity.getTipo() == null) {
            return new GatewayTesteResponseDTO(false, "Tipo de gateway nao definido.", true);
        }
        if (entity.getApiKey() == null || entity.getApiKey().isBlank()) {
            return new GatewayTesteResponseDTO(false, "apiKey nao definida.", true);
        }
        if (entity.getAmbiente() == null) {
            return new GatewayTesteResponseDTO(false, "Ambiente nao definido.", true);
        }
        return new GatewayTesteResponseDTO(true,
                "Configuracao valida. Teste de conectividade real sera feito na integracao.",
                true);
    }

    private Empresa empresaLogada() {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            return null;
        }
        return logado.getEmpresa();
    }

    private EmpresaGatewayConfig findOwned(Long id) {
        EmpresaGatewayConfig entity = repository.findById(id);
        if (entity == null || !Boolean.TRUE.equals(entity.getAtivo())) {
            throw new NotFoundException("Configuração de gateway não encontrada");
        }
        Empresa empresaLogada = empresaLogada();
        if (entity.getEmpresa() == null || empresaLogada == null
                || !entity.getEmpresa().getId().equals(empresaLogada.getId())) {
            throw new NotFoundException("Configuração de gateway não encontrada");
        }
        return entity;
    }

    private TipoGateway parseTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new WebApplicationException("Tipo de gateway obrigatório", Response.Status.BAD_REQUEST);
        }
        try {
            return TipoGateway.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    "Tipo de gateway inválido (esperado ABACATE_PAY|ASAAS): " + tipo,
                    Response.Status.BAD_REQUEST);
        }
    }

    private AmbienteGateway parseAmbiente(String ambiente) {
        if (ambiente == null || ambiente.trim().isEmpty()) {
            throw new WebApplicationException("Ambiente obrigatório", Response.Status.BAD_REQUEST);
        }
        try {
            return AmbienteGateway.valueOf(ambiente.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    "Ambiente inválido (esperado SANDBOX|PRODUCAO): " + ambiente,
                    Response.Status.BAD_REQUEST);
        }
    }

    private String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private GatewayConfigResponseDTO toResponse(EmpresaGatewayConfig c) {
        return new GatewayConfigResponseDTO(
                c.getId(),
                c.getTipo() == null ? null : c.getTipo().name(),
                c.getAmbiente() == null ? null : c.getAmbiente().name(),
                c.getHabilitado(),
                mascarar(c.getApiKey()),
                c.getApiSecret() != null && !c.getApiSecret().isBlank());
    }

    private String mascarar(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return "—";
        }
        String ultimos = apiKey.length() <= 4 ? apiKey : apiKey.substring(apiKey.length() - 4);
        return "••••" + ultimos;
    }
}
