package k.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.ClienteInputDTO;
import k.dto.ClienteResponseDTO;
import k.model.Cliente;
import k.model.Empresa;
import k.repository.ClienteRepository;
import k.service.ClienteService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class ClienteServiceImpl implements ClienteService {

    public static final Logger LOG = Logger.getLogger(ClienteServiceImpl.class);

    @Inject
    ClienteRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    @Transactional
    public ClienteResponseDTO insertOrGet(ClienteInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        String cpf = sanitizeCpf(dto.cpf());
        validateCpf(cpf);

        Empresa empresa = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        Optional<Cliente> existing = repository.findByEmpresaAndCpf(empresa, cpf);

        if (existing.isPresent()) {
            Cliente c = existing.get();
            boolean changed = false;
            if (dto.nome() != null && !dto.nome().isBlank()) {
                c.setNome(dto.nome().trim());
                changed = true;
            }
            if (dto.email() != null && !dto.email().isBlank()) {
                c.setEmail(dto.email().trim());
                changed = true;
            }
            if (changed) {
                LOG.info("Cliente atualizado id=" + c.getId());
            }
            return toResponse(c);
        }

        Cliente entity = new Cliente();
        entity.setCpf(cpf);
        entity.setNome(dto.nome() == null || dto.nome().isBlank() ? null : dto.nome().trim());
        entity.setEmail(dto.email() == null || dto.email().isBlank() ? null : dto.email().trim());
        entity.setEmpresa(empresa);
        repository.persist(entity);
        LOG.info("Cliente criado id=" + entity.getId());
        return toResponse(entity);
    }

    @Override
    public ClienteResponseDTO findByCpf(String cpf) {
        String sanitized = sanitizeCpf(cpf);
        validateCpf(sanitized);
        Empresa empresa = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        Cliente c = repository.findByEmpresaAndCpf(empresa, sanitized)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        return toResponse(c);
    }

    @Override
    public List<ClienteResponseDTO> getAll() {
        Empresa empresa = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        return repository.findByEmpresa(empresa).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cliente entity = findOwned(id);
        entity.setAtivo(false);
    }

    private Cliente findOwned(Long id) {
        Cliente entity = repository.findById(id);
        if (entity == null || !Boolean.TRUE.equals(entity.getAtivo())) {
            throw new NotFoundException("Cliente não encontrado");
        }
        Empresa empresaLogada = usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa();
        if (entity.getEmpresa() == null || empresaLogada == null
                || !entity.getEmpresa().getId().equals(empresaLogada.getId())) {
            throw new NotFoundException("Cliente não encontrado");
        }
        return entity;
    }

    private String sanitizeCpf(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }

    // TODO: validar dígito verificador do CPF
    private void validateCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            throw new WebApplicationException(
                    "CPF inválido: deve conter 11 dígitos numéricos",
                    Response.Status.BAD_REQUEST);
        }
    }

    private ClienteResponseDTO toResponse(Cliente c) {
        return new ClienteResponseDTO(c.getId(), c.getCpf(), c.getNome(), c.getEmail());
    }
}
