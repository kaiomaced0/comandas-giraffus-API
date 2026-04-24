package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import k.dto.ClienteDTO;
import k.dto.ClienteResponseDTO;
import k.exception.BusinessException;
import k.model.Cliente;
import k.model.Usuario;
import k.repository.ClienteRepository;
import k.service.ClienteService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class ClienteServiceImpl implements ClienteService {

    @Inject
    ClienteRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<ClienteResponseDTO> getAll() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findByEmpresa(u.getEmpresa()).stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO getByCpf(String cpf) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Cliente c = repository.findByEmpresaAndCpf(u.getEmpresa(), cpf);
        if (c == null) {
            throw new NotFoundException("Cliente não encontrado");
        }
        return new ClienteResponseDTO(c);
    }

    @Override
    @Transactional
    public Response insertOrFind(ClienteDTO dto) {
        if (dto == null || dto.cpf() == null || dto.cpf().isBlank()) {
            throw new BusinessException("cpf é obrigatório");
        }
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Cliente existente = repository.findByEmpresaAndCpf(u.getEmpresa(), dto.cpf());
        if (existente != null) {
            boolean atualizado = false;
            if (dto.nome() != null && !dto.nome().isBlank()
                    && !dto.nome().equals(existente.getNome())) {
                existente.setNome(dto.nome());
                atualizado = true;
            }
            if (dto.email() != null && !dto.email().isBlank()
                    && !dto.email().equals(existente.getEmail())) {
                existente.setEmail(dto.email());
                atualizado = true;
            }
            return Response.ok(new ClienteResponseDTO(existente))
                    .header("X-Idempotent", atualizado ? "updated" : "reused")
                    .build();
        }
        Cliente c = new Cliente();
        c.setEmpresa(u.getEmpresa());
        c.setCpf(dto.cpf());
        c.setNome(dto.nome());
        c.setEmail(dto.email());
        repository.persist(c);
        return Response.status(Response.Status.CREATED)
                .entity(new ClienteResponseDTO(c))
                .build();
    }
}
